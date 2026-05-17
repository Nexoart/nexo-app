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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Encontrar a nova lista criada pelo Antigravity
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewFeed)

        // 2. Definir o comportamento da lista
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 3. Consumir API Real em vez dos dados falsos
        RetrofitClient.getApiService(requireContext()).getPosts().enqueue(object : Callback<List<PostBackend>> {
            override fun onResponse(call: Call<List<PostBackend>>, response: Response<List<PostBackend>>) {
                if (response.isSuccessful) {
                    val postsBackend = response.body() ?: emptyList()
                    
                    // 4. Mapear de PostBackend para a nossa classe visual (Post)
                    // 4. Mapear de PostBackend para a nossa classe visual (Post)
                    val listaFeed = postsBackend.map { postBackend ->
                        Post(
                            artistName = "Artista ${postBackend.idArtista}",
                            usernameTime = "@artista${postBackend.idArtista} • Agora",
                            caption = postBackend.descricao,
                            postImageUrl = postBackend.urlImagem, // Repassando a URL recebida da API
                            profileImageResId = R.drawable.marcy // Imagem placeholder
                        )
                    }

                    // 5. Ligar a lista ao Adapter para desenhar no ecrã
                    recyclerView.adapter = PostAdapter(listaFeed)
                } else {
                    Toast.makeText(requireContext(), "Erro ao carregar posts do Feed", Toast.LENGTH_SHORT).show()
                    Log.e("HomeFragment", "Erro na API: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<PostBackend>>, t: Throwable) {
                // Log de erro no onFailure para ajudar no debug
                Log.e("HomeFragment", "Falha de conexão em getPosts: ${t.message}", t)
                Toast.makeText(requireContext(), "Falha na conexão com o Feed", Toast.LENGTH_SHORT).show()
            }
        })

        // 6. Configurar o clique do FAB para abrir a NewPostActivity
        val fabNewPost = view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabNewPost)
        fabNewPost.setOnClickListener {
            val intent = android.content.Intent(requireContext(), NewPostActivity::class.java)
            startActivity(intent)
        }
    }
}