package com.example.nexoapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient

object RetrofitClient {

    val apiService: Any
        get() {
            TODO()
        }

    // IP mágico para o emulador enxergar o seu PC (Spring Boot)
    private const val BASE_URL = "http://10.0.2.2:8080/"

    // Transformamos apiService em uma função para podermos receber o Context
    // e assim ler o Token do SharedPreferences
    fun getApiService(context: android.content.Context): ApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val sharedPref = context.getSharedPreferences("NexoAppPrefs", android.content.Context.MODE_PRIVATE)
                val token = sharedPref.getString("AUTH_TOKEN", null)
                
                val requestBuilder = chain.request().newBuilder()
                if (!token.isNullOrEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
                chain.proceed(requestBuilder.build())
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()) // Transforma JSON em Objeto e vice-versa
            .build()
            .create(ApiService::class.java)
    }
}