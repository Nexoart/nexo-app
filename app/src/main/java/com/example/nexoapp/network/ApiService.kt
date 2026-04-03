package com.example.nexoapp.network

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// --- AS SUAS DATA CLASSES (Os moldes dos dados) ---
data class LoginRequest(val email: String, val senha: String)

data class LoginResponse(val token: String)

interface ApiService {

    // Rota antiga de Login (que você já tinha)
    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    // Nova Rota de Cadastro que aponta pro seu Controller do Spring Boot
    @POST("auth/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<Void>
}