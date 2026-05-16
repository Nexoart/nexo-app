package com.example.nexoapp

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class NewPostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnPublish = findViewById<MaterialButton>(R.id.btnPublish)

        btnBack.setOnClickListener {
            finish()
        }

        btnPublish.setOnClickListener {
            Toast.makeText(this, "Arte enviada com sucesso!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
