package com.example.nexoapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nexoapp.network.LoginResponse
import com.example.nexoapp.network.RegisterRequest
import com.example.nexoapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val switchArtist = findViewById<Switch>(R.id.switchArtist)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val isArtista = switchArtist.isChecked

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validação de E-mail
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Por favor, insira um e-mail válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validação de Senha (mínimo de 6 caracteres)
            if (password.length < 6) {
                Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = RegisterRequest(name, email, password, isArtista)

            RetrofitClient.getApiService(this).registerUser(request).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val token = response.body()?.token
                        val id = response.body()?.id
                        
                        if (token != null && id != null) {
                            val sharedPref = getSharedPreferences("NexoAppPrefs", android.content.Context.MODE_PRIVATE)
                            with (sharedPref.edit()) {
                                putString("AUTH_TOKEN", token)
                                putLong("USER_ID", id)
                                apply()
                            }
                            Toast.makeText(this@RegisterActivity, "Cadastro realizado!", Toast.LENGTH_SHORT).show()
                            val intent = android.content.Intent(this@RegisterActivity, EditProfileActivity::class.java)
                            intent.putExtra("IS_ONBOARDING", true)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@RegisterActivity, "Erro: ID ou Token não recebidos do servidor!", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        // Exibir o erro real retornado pelo servidor
                        val errorMsg = response.errorBody()?.string() ?: "Erro ao cadastrar"
                        Toast.makeText(this@RegisterActivity, errorMsg, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, "Falha na conexão: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}