package com.example.nexoapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nexoapp.network.LoginRequest
import com.example.nexoapp.network.LoginResponse
import com.example.nexoapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Referenciando os elementos do seu XML
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val senha = etPassword.text.toString()

            // 1. Validação de campos vazios (Sempre primeiro!)
            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. O seu "Caminho de Emergência" (Login Fake para a Cilene ver)
            if (email == "admin@nexo.com" && senha == "1234") {
                Toast.makeText(this, "Acesso Liberado (Modo Demo)", Toast.LENGTH_SHORT).show()

                // MUDANÇA AQUI: Vai para o Onboarding em vez da Home
                val intent = Intent(this, OnboardingActivity::class.java)
                startActivity(intent)
                finish()
                return@setOnClickListener // Para o código parar aqui e não tentar o Retrofit
            }

            // 3. O "Caminho Real" (Retrofit - Só roda se o email não for o admin)
            val loginRequest = LoginRequest(email, senha)
            RetrofitClient.apiService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val token = response.body()?.token
                        Toast.makeText(this@MainActivity, "Login Sucesso! Token: $token", Toast.LENGTH_LONG).show()

                        // MUDANÇA AQUI: Vai para o Onboarding em vez da Home
                        val intent = Intent(this@MainActivity, OnboardingActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@MainActivity, "Erro: Usuário ou senha inválidos", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Falha na conexão: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}