package com.example.nexoapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.example.nexoapp.network.RetrofitClient
import com.example.nexoapp.network.UserBackend
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

class EditProfileActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null
    private var selectedCoverUri: Uri? = null
    private lateinit var imgEditProfile: ImageView
    private lateinit var imgEditCover: ImageView
    private var currentUserInfo: UserBackend? = null
    private var currentUserId: Long = 1L
    private var isCroppingCover = false

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            isCroppingCover = false
            startCrop(uri)
        }
    }

    private val pickCoverMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            isCroppingCover = true
            startCrop(uri)
        }
    }

    private fun startCrop(uri: Uri) {
        val destinationUri = Uri.fromFile(File(cacheDir, "cropped_${System.currentTimeMillis()}.jpg"))
        com.yalantis.ucrop.UCrop.of(uri, destinationUri)
            .withAspectRatio(if (isCroppingCover) 16f else 1f, if (isCroppingCover) 9f else 1f)
            .start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == com.yalantis.ucrop.UCrop.REQUEST_CROP) {
            val resultUri = com.yalantis.ucrop.UCrop.getOutput(data!!)
            if (isCroppingCover) {
                selectedCoverUri = resultUri
                imgEditCover.setImageURI(resultUri)
            } else {
                selectedImageUri = resultUri
                imgEditProfile.setImageURI(resultUri)
            }
        } else if (resultCode == com.yalantis.ucrop.UCrop.RESULT_ERROR) {
            val error = com.yalantis.ucrop.UCrop.getError(data!!)
            Toast.makeText(this, "Erro no recorte: ${error?.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val cardEditProfile = findViewById<CardView>(R.id.cardEditProfile)
        imgEditProfile = findViewById(R.id.imgEditProfile)
        imgEditCover = findViewById(R.id.imgEditCover)
        val btnSelectCover = findViewById<MaterialButton>(R.id.btnSelectCover)
        val editNewName = findViewById<EditText>(R.id.editNewName)
        val editNewBio = findViewById<EditText>(R.id.editNewBio)
        val btnSaveProfile = findViewById<MaterialButton>(R.id.btnSaveProfile)

        val sharedPref = getSharedPreferences("NexoAppPrefs", android.content.Context.MODE_PRIVATE)
        currentUserId = sharedPref.getLong("USER_ID", 1L)

        // Buscar dados atuais para preencher
        RetrofitClient.getApiService(this).getUserProfile(currentUserId).enqueue(object : Callback<UserBackend> {
            override fun onResponse(call: Call<UserBackend>, response: Response<UserBackend>) {
                if (response.isSuccessful) {
                    currentUserInfo = response.body()
                    currentUserInfo?.let {
                        editNewName.setText(it.name)
                        editNewBio.setText(it.bio ?: "")
                        Glide.with(this@EditProfileActivity).load(it.profileImage).placeholder(R.drawable.marcy).into(imgEditProfile)
                        Glide.with(this@EditProfileActivity).load(it.coverImage).placeholder(R.drawable.shiorivolt).into(imgEditCover)
                    }
                }
            }
            override fun onFailure(call: Call<UserBackend>, t: Throwable) {
                Toast.makeText(this@EditProfileActivity, "Falha de conexão com o servidor", Toast.LENGTH_SHORT).show()
            }
        })

        cardEditProfile.setOnClickListener {
            pickMedia.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        btnSelectCover.setOnClickListener {
            pickCoverMedia.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        btnSaveProfile.setOnClickListener {
            val name = editNewName.text.toString()
            val bio = editNewBio.text.toString()

            btnSaveProfile.isEnabled = false
            btnSaveProfile.text = "Salvando..."

            startUploads(name, bio, btnSaveProfile)
        }
    }

    private fun startUploads(name: String, bio: String, btnSaveProfile: MaterialButton) {
        if (selectedCoverUri != null) {
            uploadCoverThenProfile(name, bio, btnSaveProfile)
        } else if (selectedImageUri != null) {
            uploadImageAndUpdateProfile(name, bio, currentUserInfo?.coverImage, btnSaveProfile)
        } else {
            updateProfileCall(name, bio, currentUserInfo?.profileImage, currentUserInfo?.coverImage, btnSaveProfile)
        }
    }

    private fun uploadCoverThenProfile(name: String, bio: String, btnSaveProfile: MaterialButton) {
        val file = getFileFromUri(selectedCoverUri!!) ?: return
        val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        RetrofitClient.getApiService(this).uploadImage(body).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val coverUrl = response.body()?.string() ?: ""
                    if (selectedImageUri != null) {
                        uploadImageAndUpdateProfile(name, bio, coverUrl, btnSaveProfile)
                    } else {
                        updateProfileCall(name, bio, currentUserInfo?.profileImage, coverUrl, btnSaveProfile)
                    }
                } else {
                    Toast.makeText(this@EditProfileActivity, "Erro no upload da capa", Toast.LENGTH_SHORT).show()
                    btnSaveProfile.isEnabled = true
                    btnSaveProfile.text = "Salvar e Continuar"
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                btnSaveProfile.isEnabled = true
            }
        })
    }

    private fun uploadImageAndUpdateProfile(name: String, bio: String, coverUrl: String?, btnSaveProfile: MaterialButton) {
        val file = getFileFromUri(selectedImageUri!!) ?: return
        val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        RetrofitClient.getApiService(this).uploadImage(body).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val profileUrl = response.body()?.string() ?: ""
                    updateProfileCall(name, bio, profileUrl, coverUrl, btnSaveProfile)
                } else {
                    Toast.makeText(this@EditProfileActivity, "Erro no upload do perfil", Toast.LENGTH_SHORT).show()
                    btnSaveProfile.isEnabled = true
                    btnSaveProfile.text = "Salvar e Continuar"
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                btnSaveProfile.isEnabled = true
            }
        })
    }

    private fun updateProfileCall(name: String, bio: String, profileImageUrl: String?, coverImageUrl: String?, btnSaveProfile: MaterialButton) {
        val updatedUser = UserBackend(
            id = currentUserId,
            name = if (name.isBlank()) currentUserInfo?.name ?: "Usuário" else name,
            isArtista = currentUserInfo?.isArtista ?: true,
            profileImage = profileImageUrl,
            bio = bio,
            coverImage = coverImageUrl,
            seguidoresCount = currentUserInfo?.seguidoresCount ?: 0,
            curtidasCount = currentUserInfo?.curtidasCount ?: 0
        )

        RetrofitClient.getApiService(this).updateUserProfile(currentUserId, updatedUser).enqueue(object : Callback<UserBackend> {
            override fun onResponse(call: Call<UserBackend>, response: Response<UserBackend>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EditProfileActivity, "Perfil salvo!", Toast.LENGTH_SHORT).show()
                    val isOnboarding = intent.getBooleanExtra("IS_ONBOARDING", false)
                    if (isOnboarding) {
                        startActivity(Intent(this@EditProfileActivity, HomeActivity::class.java))
                    }
                    finish()
                } else {
                    Toast.makeText(this@EditProfileActivity, "Erro HTTP: ${response.code()}", Toast.LENGTH_LONG).show()
                    btnSaveProfile.isEnabled = true
                    btnSaveProfile.text = "Salvar e Continuar"
                }
            }
            override fun onFailure(call: Call<UserBackend>, t: Throwable) {
                btnSaveProfile.isEnabled = true
            }
        })
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
