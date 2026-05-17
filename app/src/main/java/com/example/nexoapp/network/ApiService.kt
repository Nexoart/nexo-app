package com.example.nexoapp.network

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

// --- AS SUAS DATA CLASSES (Os moldes dos dados) ---
data class LoginRequest(val email: String, val password: String)

data class LoginResponse(val token: String)

data class PostBackend(
    val id: Long?, // Mudado de Long para Long? para aceitar null
    val idArtista: Long,
    val urlImagem: String,
    val descricao: String,
    val timestamp: String?
)

interface ApiService {

    // Rota antiga de Login (que você já tinha)
    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    // Nova Rota de Cadastro que aponta pro seu Controller do Spring Boot
    @POST("auth/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<Void>

    // Nova rota para obter os posts do Feed
    @GET("posts")
    fun getPosts(): Call<List<PostBackend>>

    // Rota de upload de imagem
    @Multipart
    @POST("upload")
    fun uploadImage(@Part file: MultipartBody.Part): Call<ResponseBody>

    // Rota para criar um post
    @POST("posts")
    fun createPost(@Body post: PostBackend): Call<PostBackend>
}