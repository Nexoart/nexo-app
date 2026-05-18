package com.example.nexoapp

import Post
import PostAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nexoapp.network.PostBackend
import com.example.nexoapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var allPosts: List<Post> = emptyList()
    private lateinit var postAdapter: PostAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Encontrar a nova lista criada pelo Antigravity
        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewFeed)

        // 2. Definir o comportamento da lista
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        postAdapter = PostAdapter(emptyList())
        recyclerView.adapter = postAdapter

        // 6. Configurar o clique do FAB para abrir a NewPostActivity
        val fabNewPost = view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabNewPost)
        fabNewPost.setOnClickListener {
            val intent = android.content.Intent(requireContext(), NewPostActivity::class.java)
            startActivity(intent)
        }

        // 7. Configurar lógica das Tags
        setupTags(view)
    }

    override fun onResume() {
        super.onResume()
        // Consumir API toda vez que a aba/fragment voltar a ficar visível
        loadPosts()
    }

    private fun loadPosts() {
        RetrofitClient.getApiService(requireContext()).getPosts().enqueue(object : Callback<List<PostBackend>> {
            override fun onResponse(call: Call<List<PostBackend>>, response: Response<List<PostBackend>>) {
                if (response.isSuccessful) {
                    val postsBackend = response.body() ?: emptyList()
                    
                    allPosts = postsBackend.map { postBackend ->
                        Post(
                            id = postBackend.id,
                            artistName = postBackend.artista.name,
                            usernameTime = "@${postBackend.artista.name.lowercase().replace(" ", "")} • Agora",
                            caption = postBackend.descricao,
                            postImageUrl = postBackend.urlImagem,
                            profileImageUrl = postBackend.artista.profileImage,
                            likesCount = postBackend.curtidasCount // Dinâmico!
                        )
                    }
                    postAdapter.updateData(allPosts)
                } else {
                    Toast.makeText(requireContext(), "Erro ao carregar posts do Feed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<PostBackend>>, t: Throwable) {
                Toast.makeText(requireContext(), "Falha na conexão com o Feed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupTags(view: View) {
        val tags = listOf(
            view.findViewById<android.widget.TextView>(R.id.tagAll),
            view.findViewById<android.widget.TextView>(R.id.tagConcept),
            view.findViewById<android.widget.TextView>(R.id.tag3D),
            view.findViewById<android.widget.TextView>(R.id.tagAnime),
            view.findViewById<android.widget.TextView>(R.id.tagPixel)
        )

        tags.forEach { tagView ->
            tagView.setOnClickListener {
                // Resetar todos para cor desligada
                tags.forEach { t ->
                    t.setBackgroundResource(R.drawable.bg_tag_unselected)
                    t.setTextColor(android.graphics.Color.parseColor("#888888"))
                }
                
                // Ligar o clicado
                tagView.setBackgroundResource(R.drawable.bg_tag_selected)
                tagView.setTextColor(android.graphics.Color.WHITE)

                // Filtrar lista
                val query = tagView.text.toString()
                if (query == "Todos") {
                    postAdapter.updateData(allPosts)
                } else {
                    val filtered = allPosts.filter { post -> 
                        post.caption.contains(query, ignoreCase = true) 
                    }
                    postAdapter.updateData(filtered)
                }
            }
        }
    }
}