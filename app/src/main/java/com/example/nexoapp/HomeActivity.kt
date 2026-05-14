package com.example.nexoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    substituirFragment(HomeFragment())
                    true
                }
                R.id.nav_discover -> {
                    substituirFragment(SearchFragment())
                    true
                }
                R.id.nav_messages -> { // <-- ADICIONAMOS O NOVO BOTÃO AQUI
                    substituirFragment(MessagesFragment())
                    true
                }
                R.id.nav_profile -> {
                    substituirFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }



        // Carrega a Home logo ao abrir
        if (savedInstanceState == null) {
            substituirFragment(HomeFragment())
        }
    }

    // Função agora fora do onCreate, como uma função da classe
    private fun substituirFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}