package com.example.nexoapp

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MessagesFragment : Fragment(R.layout.fragment_messages) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewMessages)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val list = listOf(
            Conversation("Ikusi", "Mandei o arquivo psd pra você analisar.", R.drawable.marcy),
            Conversation("Dana", "Obrigada! Ficou incrível o concept.", R.drawable.shiorivolt),
            Conversation("Artwins", "Bora jogar algo mais tarde?", R.drawable.marcy),
            Conversation("Nexo Team", "Bem-vindo à comunidade NexoArt!", R.drawable.shiorivolt)
        )
        recyclerView.adapter = MessageAdapter(list)
    }
}