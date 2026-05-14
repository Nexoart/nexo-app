package com.example.nexoapp

import Post
import PostAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // --- CONFIGURAR FOTO DE PERFIL E CAPA ---
        val imgProfile = view.findViewById<android.widget.ImageView>(R.id.imageProfile)
        imgProfile.setImageResource(R.drawable.icone) // Coloquei o seu icone aqui!

        val imgCover = view.findViewById<android.widget.ImageView>(R.id.imageCover)
        imgCover.setImageResource(R.drawable.shiorivolt) // Escolha uma arte legal para ser a capa do perfil

        // --- CONFIGURAR O RECYCLERVIEW (O Grid) ---
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewProfile)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        // Agora a lista é só das imagens puras!
        val listaArtes = listOf(
            R.drawable.syther,
            R.drawable.shiorivolt,
            R.drawable.ilustration1
        )
        recyclerView.adapter = PortfolioAdapter(listaArtes)
        // --- CONFIGURAR O CHAT FAKE ---
        val btnMensagem = view.findViewById<Button>(R.id.buttonMessage)
        btnMensagem.setOnClickListener {
            val intent = Intent(requireContext(), ChatFakeActivity::class.java)
            startActivity(intent)
        }
    }
}