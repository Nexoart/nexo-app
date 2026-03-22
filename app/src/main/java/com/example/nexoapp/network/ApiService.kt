#!/usr/bin/env kotlin
package com.example.nexoapp.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

// Modelo de dados para o login
data class LoginRequest(val email: String, val senha: String)
data class LoginResponse(val token: String)

interface ApiService {
    @POST("auth/login") // O endpoint que o Lucas está criando
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}
