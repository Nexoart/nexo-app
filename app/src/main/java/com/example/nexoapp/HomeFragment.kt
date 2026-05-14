package com.example.nexoapp

import Post
import PostAdapter
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Encontrar a nova lista criada pelo Antigravity
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewFeed)

        // 2. Definir o comportamento da lista
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 3. Criar os dados falsos do MVP
        val listaFeed = listOf(
            Post("Ikusi", "@ikusi", "Arte post-apocalíptica #conceptart", R.drawable.marcy, R.drawable.marcy),
            Post("Rafilskz", "@rafilskz", "Explosão de cores e magia", R.drawable.marcy, R.drawable.marcy),
            Post("Filipe Arts", "@filipe_arts", "Estudo anatómico e perspetiva", R.drawable.marcy, R.drawable.anatomia)
        )

        // 4. Ligar a lista ao Adapter para desenhar no ecrã
        recyclerView.adapter = PostAdapter(listaFeed)

        // 5. Configurar o clique do FAB para abrir a NewPostActivity
        val fabNewPost = view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabNewPost)
        fabNewPost.setOnClickListener {
            val intent = android.content.Intent(requireContext(), NewPostActivity::class.java)
            startActivity(intent)
        }
    }
}