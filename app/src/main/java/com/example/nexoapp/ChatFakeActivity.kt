package com.example.nexoapp

import android.os.Bundle
import android.widget.ImageView // <-- Agora é ImageView
import androidx.appcompat.app.AppCompatActivity

class ChatFakeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_fake)

        // O id correto agora é iconBack
        val btnBack = findViewById<ImageView>(R.id.iconBack)
        btnBack.setOnClickListener {
            finish() // Destrói o chat e volta pro perfil graciosamente
        }
    }
}