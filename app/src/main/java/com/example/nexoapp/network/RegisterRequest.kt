package com.example.nexoapp.network

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val isArtista: Boolean
)