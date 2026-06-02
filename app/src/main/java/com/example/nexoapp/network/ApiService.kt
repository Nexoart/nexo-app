package com.example.nexoapp.network

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Headers

// --- AS SUAS DATA CLASSES (Os moldes dos dados) ---
data class LoginRequest(val email: String, val password: String)

data class LoginResponse(
    @com.google.gson.annotations.SerializedName("token") val token: String?,
    @com.google.gson.annotations.SerializedName("id") val id: Long?
)

data class UserBackend(
    val id: Long, 
    val name: String, 
    val isArtista: Boolean,
    val profileImage: String?,
    val bio: String?,
    val coverImage: String? = null,
    val seguidoresCount: Int = 0,
    val curtidasCount: Int = 0
)

data class PostBackend(
    val id: Long?, // Mudado de Long para Long? para aceitar null
    val artista: UserBackend?, // Tornado nullable para não sobrescrever dados no BD
    val urlImagem: String?,
    val descricao: String,
    val timestamp: String?,
    val curtidasCount: Int = 0
)

data class ComentarioRequest(val autor: String, val texto: String)
data class ComentarioBackend(val id: Long?, val autor: String, val texto: String)

data class BotpressRequest(val message: String)
data class BotpressResponse(val responses: List<BotMessage>)
data class BotMessage(val text: String)

interface ApiService {

    // Rota antiga de Login (que você já tinha)
    @Headers("No-Authentication: true")
    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    // Nova Rota de Cadastro que aponta pro seu Controller do Spring Boot
    @Headers("No-Authentication: true")
    @POST("auth/register")
    fun registerUser(@Body request: RegisterRequest): Call<LoginResponse>

    // Nova rota para obter os posts do Feed
    @GET("posts")
    fun getPosts(): Call<List<PostBackend>>

    // Rota de upload de imagem
    @Multipart
    @POST("upload")
    fun uploadImage(@Part file: MultipartBody.Part): Call<ResponseBody>

    // Rota para criar um post
    @POST("posts")
    fun createPost(@retrofit2.http.Query("userId") userId: Long, @Body post: PostBackend): Call<PostBackend>

    // Rota de curtidas (Like)
    @PUT("posts/{id}/like")
    fun likePost(@Path("id") postId: Long): Call<PostBackend>

    // Rota para adicionar comentário
    @POST("posts/{id}/comments")
    fun addComment(@Path("id") postId: Long, @Body comment: ComentarioRequest): Call<PostBackend>

    // Rota para buscar comentários
    @GET("posts/{id}/comments")
    fun getComments(@Path("id") postId: Long): Call<List<ComentarioBackend>>

    // Buscar Perfil do Usuário
    @GET("users/{id}")
    fun getUserProfile(@Path("id") userId: Long): Call<UserBackend>

    // Atualizar Perfil do Usuário
    @PUT("users/{id}")
    fun updateUserProfile(@Path("id") userId: Long, @Body user: UserBackend): Call<UserBackend>

    // Rota para o Webhook/API do seu bot no Botpress
    @POST("https://chat.botpress.cloud/api/v1/bots/f5169dc1-d1c4-4bc4-bda6-8dbf82d3edc9/converse")
    fun sendMessageToBot(@Body request: BotpressRequest): Call<BotpressResponse>
}