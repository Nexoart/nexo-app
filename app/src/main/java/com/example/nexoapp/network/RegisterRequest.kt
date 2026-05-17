package com.example.nexoapp.network // Ajuste se o pacote for diferente

// Essa Data Class é a irmã gêmea da sua Entity lá do Spring Boot

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)