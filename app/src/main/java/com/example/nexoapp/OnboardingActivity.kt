package com.example.nexoapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class OnboardingActivity : AppCompatActivity() {

    private var selectedStyle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val card2D = findViewById<MaterialCardView>(R.id.card2D)
        val card3D = findViewById<MaterialCardView>(R.id.card3D)
        val cardPixel = findViewById<MaterialCardView>(R.id.cardPixel)
        val cardVetor = findViewById<MaterialCardView>(R.id.cardVetor)
        val cardUX = findViewById<MaterialCardView>(R.id.cardUX)
        val cardConcept = findViewById<MaterialCardView>(R.id.cardConcept)
        val buttonEnter = findViewById<MaterialButton>(R.id.buttonEnter)

        val cards = listOf(
            Pair(card2D, "2D"),
            Pair(card3D, "3D"),
            Pair(cardPixel, "Pixel Art"),
            Pair(cardVetor, "Vetor"),
            Pair(cardUX, "UI/UX"),
            Pair(cardConcept, "Concept Art")
        )

        cards.forEach { (card, styleName) ->
            card.setOnClickListener {
                if (selectedStyle == styleName) {
                    selectedStyle = null
                    card.setStrokeColor(android.content.res.ColorStateList.valueOf(Color.parseColor("#222222")))
                    card.strokeWidth = dpToPx(1)
                } else {
                    selectedStyle = styleName
                    // Reset all other cards
                    cards.forEach { (c, _) ->
                        c.setStrokeColor(android.content.res.ColorStateList.valueOf(Color.parseColor("#222222")))
                        c.strokeWidth = dpToPx(1)
                    }
                    // Highlight selected card
                    card.setStrokeColor(android.content.res.ColorStateList.valueOf(Color.parseColor("#10B981")))
                    card.strokeWidth = dpToPx(3)
                }
            }
        }

        buttonEnter.setOnClickListener {
            // Salva a seleção no SharedPreferences para o feed filtrar
            val sharedPref = getSharedPreferences("NexoAppPrefs", android.content.Context.MODE_PRIVATE)
            sharedPref.edit().putString("EXPLORE_STYLE", selectedStyle).apply()

            // Vai para a HomeActivity
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }
}
