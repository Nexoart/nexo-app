package com.example.nexoapp

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
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
        val btnLogin = findViewById<com.google.android.material.button.MaterialButton>(R.id.btnLogin)
        val tvGoToRegister = findViewById<TextView>(R.id.tvGoToRegister)

        tvGoToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

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

                // MUDANÇA AQUI: Vai para a HomeActivity
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
                return@setOnClickListener // Para o código parar aqui e não tentar o Retrofit
            }

            // 3. O "Caminho Real" (Retrofit - Só roda se o email não for o admin)
            val loginRequest = LoginRequest(email, senha)
            RetrofitClient.getApiService(this@MainActivity).login(loginRequest).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val token = response.body()?.token
                        
                        // Salvar o Token no SharedPreferences
                        if (token != null) {
                            val sharedPref = getSharedPreferences("NexoAppPrefs", android.content.Context.MODE_PRIVATE)
                            with (sharedPref.edit()) {
                                putString("AUTH_TOKEN", token)
                                apply()
                            }
                        }

                        Toast.makeText(this@MainActivity, "Login Sucesso! Token: $token", Toast.LENGTH_LONG).show()

                        // MUDANÇA AQUI: Vai para a HomeActivity
                        val intent = Intent(this@MainActivity, HomeActivity::class.java)
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