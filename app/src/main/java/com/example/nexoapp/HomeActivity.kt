package com.example.nexoapp

import Post
import PostAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewFeed)

        // O RecyclerView precisa saber como rolar a tela (de cima para baixo)
        recyclerView.layoutManager = LinearLayoutManager(this)

        //  lista falsa para teste
        val listaFalsa = listOf(
            Post("flip", "@filipe_arts • 2hr", "Estudando anatomia hoje!", R.drawable.anatomia, R.drawable.test ),
            Post("PH", "@rafilskz • 5hr", "Primeiro teste do feed rolando solto!", R.drawable.marcy, R.drawable.luz),
            Post("Cilene", "@prof_cilene • 1d", "Outro teste do feed rolando solto!", R.drawable.florest, R.drawable.eda)
        )

        // Ligando o Adapter ao RecyclerView
        recyclerView.adapter = PostAdapter(listaFalsa)
    }
}