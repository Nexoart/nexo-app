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

    // a) ActivityResultLauncher para abrir a galeria
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            imgPreview.setImageURI(uri)
            imgPreview.scaleType = ImageView.ScaleType.CENTER_CROP
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
            pickImageLauncher.launch("image/*")
        }

        btnBack.setOnClickListener {
            finish()
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

                        // d) Criar objeto do post
                        val post = PostBackend(
                            id = null,
                            artista = com.example.nexoapp.network.UserBackend(id = 1L, name = "Rafilskz", isArtista = true, profileImage = null, bio = null),
                            urlImagem = cloudinaryUrl,
                            descricao = descricao,
                            timestamp = null
                        )

                        RetrofitClient.getApiService(this@NewPostActivity).createPost(1L, post).enqueue(object : Callback<PostBackend> {
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
