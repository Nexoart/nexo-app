package com.example.nexoapp

import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.nexoapp.network.PostBackend
import com.example.nexoapp.network.RetrofitClient
import com.google.android.material.button.MaterialButton
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class NewPostActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null
    private lateinit var imgPreview: ImageView

    // a) ActivityResultLauncher moderno para abrir a galeria
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val destinationUri = Uri.fromFile(File(cacheDir, "post_cropped_${System.currentTimeMillis()}.jpg"))
            com.yalantis.ucrop.UCrop.of(uri, destinationUri)
                .withAspectRatio(1f, 1f)
                .start(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == com.yalantis.ucrop.UCrop.REQUEST_CROP) {
            val resultUri = com.yalantis.ucrop.UCrop.getOutput(data!!)
            if (resultUri != null) {
                selectedImageUri = resultUri
                
                // Remover limites do ícone e o tom verde
                imgPreview.layoutParams.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT
                imgPreview.layoutParams.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT
                imgPreview.scaleType = ImageView.ScaleType.CENTER_CROP
                imgPreview.clearColorFilter()
                imgPreview.imageTintList = null // CRÍTICO: remove o tint verde do XML que pintava toda a imagem carregada!
                imgPreview.requestLayout()

                com.bumptech.glide.Glide.with(this)
                    .load(resultUri)
                    .centerCrop()
                    .into(imgPreview)
            }
        } else if (resultCode == com.yalantis.ucrop.UCrop.RESULT_ERROR) {
            val error = com.yalantis.ucrop.UCrop.getError(data!!)
            Toast.makeText(this, "Erro no recorte: ${error?.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnPublish = findViewById<MaterialButton>(R.id.btnPublish)
        val cardUpload = findViewById<CardView>(R.id.cardUpload)
        val editDescription = findViewById<EditText>(R.id.editDescription)
        
        // Vamos pegar a ImageView que está dentro do CardView
        imgPreview = cardUpload.getChildAt(0) as ImageView

        // Clique no CardView para selecionar imagem
        cardUpload.setOnClickListener {
            pickImageLauncher.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        btnBack.setOnClickListener {
            finish()
        }

        // Lógica das tags sugeridas
        val tags = listOf(
            findViewById<android.widget.TextView>(R.id.tagAddConcept),
            findViewById<android.widget.TextView>(R.id.tagAdd3D),
            findViewById<android.widget.TextView>(R.id.tagAddAnime),
            findViewById<android.widget.TextView>(R.id.tagAddPixel)
        )
        tags.forEach { tagView ->
            tagView.setOnClickListener {
                val currentText = editDescription.text.toString()
                editDescription.setText("$currentText ${tagView.text} ".trimStart())
                editDescription.setSelection(editDescription.text.length)
            }
        }

        btnPublish.setOnClickListener {
            val uri = selectedImageUri
            if (uri == null) {
                Toast.makeText(this, "Selecione uma imagem primeiro!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val descricao = editDescription.text.toString()

            btnPublish.isEnabled = false
            btnPublish.text = "Enviando..."

            // Converter URI para arquivo temporário
            val file = getFileFromUri(uri)
            if (file == null) {
                Toast.makeText(this, "Erro ao ler a imagem", Toast.LENGTH_SHORT).show()
                btnPublish.isEnabled = true
                return@setOnClickListener
            }

            // Converter para MultipartBody.Part (Compatível com OkHttp 3.x)
            val mediaType = MediaType.parse("image/*")
            val requestFile = RequestBody.create(mediaType, file)
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            // b) Chamar apiService.uploadImage
            RetrofitClient.getApiService(this).uploadImage(body).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        // c) Ler a string de resposta (URL do Cloudinary)
                        val cloudinaryUrl = response.body()?.string() ?: ""

                        val sharedPref = getSharedPreferences("NexoAppPrefs", android.content.Context.MODE_PRIVATE)
                        val currentUserId = sharedPref.getLong("USER_ID", 1L)

                        // d) Criar objeto do post sem enviar o User falso (evita corromper o DB)
                        val post = PostBackend(
                            id = null,
                            artista = null, 
                            urlImagem = cloudinaryUrl,
                            descricao = descricao,
                            timestamp = null
                        )

                        RetrofitClient.getApiService(this@NewPostActivity).createPost(currentUserId, post).enqueue(object : Callback<PostBackend> {
                            override fun onResponse(call: Call<PostBackend>, response: Response<PostBackend>) {
                                if (response.isSuccessful) {
                                    Toast.makeText(this@NewPostActivity, "Post publicado com sucesso!", Toast.LENGTH_SHORT).show()
                                    // e) Fechar tela
                                    finish()
                                } else {
                                    Toast.makeText(this@NewPostActivity, "Erro ao publicar post.", Toast.LENGTH_SHORT).show()
                                    btnPublish.isEnabled = true
                                    btnPublish.text = "Publicar"
                                }
                            }

                            override fun onFailure(call: Call<PostBackend>, t: Throwable) {
                                Toast.makeText(this@NewPostActivity, "Falha na conexão.", Toast.LENGTH_SHORT).show()
                                btnPublish.isEnabled = true
                                btnPublish.text = "Publicar"
                            }
                        })
                    } else {
                        Toast.makeText(this@NewPostActivity, "Falha no upload! Erro: ${response.code()}", Toast.LENGTH_LONG).show()
                        btnPublish.isEnabled = true
                        btnPublish.text = "Publicar"
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@NewPostActivity, "Erro de rede no upload: ${t.message}", Toast.LENGTH_SHORT).show()
                    btnPublish.isEnabled = true
                    btnPublish.text = "Publicar"
                }
            })
        }
    }

    private fun getFileFromUri(uri: Uri): File? {
        if (uri.scheme == "file") {
            return File(uri.path!!)
        }
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("upload", ".jpg", cacheDir)
            val outputStream = FileOutputStream(tempFile)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            tempFile
        } catch (e: Exception) {
            null
        }
    }
}
