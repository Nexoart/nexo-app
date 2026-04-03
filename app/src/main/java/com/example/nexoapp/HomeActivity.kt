package com.example.nexoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Certifique-se de que você tem um layout chamado activity_home.xml
        // Se não tiver, comente a linha abaixo até criar um.
        setContentView(R.layout.activity_home)
    }
}