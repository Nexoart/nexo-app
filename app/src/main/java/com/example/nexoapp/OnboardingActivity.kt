package com.example.nexoapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class OnboardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val buttonEnter = findViewById<MaterialButton>(R.id.buttonEnter)

        buttonEnter.setOnClickListener {
            // Dispara a Intent para abrir a HomeActivity
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            
            // Impede que o usuário volte para o Onboarding
            finish()
        }
    }
}
