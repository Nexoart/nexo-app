package com.example.nexoapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // IP mágico para o emulador enxergar o seu PC (Spring Boot)
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Transforma JSON em Objeto e vice-versa
            .build()
            .create(ApiService::class.java)
    }
}