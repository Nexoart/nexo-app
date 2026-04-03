package com.example.nexoapp // Confirme se o seu pacote é esse mesmo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.nexoapp.network.RegisterRequest
import com.example.nexoapp.network.RetrofitClient
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        android.widget.Toast.makeText(this, "Tentando conectar ao backend...", android.widget.Toast.LENGTH_LONG).show()

        // Rodando o teste assim que a tela abrir
        testarCadastroBackend()
    }

    private fun testarCadastroBackend() {

        val fakeUser = RegisterRequest(
            name = "Raphael",
            email = "ph@teste.com",
            password = "senha-super-segura",
            alias = "Rafilskz"
        )

        // para a internet não travar a tela do celular.
        lifecycleScope.launch {
            try {
                // Fazendo a chamada para o Spring Boot
                val response = RetrofitClient.apiService.registerUser(fakeUser)


                if (response.isSuccessful) {
                    Log.d("NEXO_API", "✅ Sucesso! Artista salvo no banco!")
                } else {
                    Log.e("NEXO_API", "❌ Erro no Spring Boot. Código: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("NEXO_API", "🧨 Falha de conexão. O Tomcat tá rodando? Erro: ${e.message}")
            }
        }
    }
}