package com.example.nexoapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nexoapp.network.RetrofitClient
import com.example.nexoapp.network.UserBackend
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    // Variável para checar se algo mudou
    private var currentUserInfo: UserBackend? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val imgProfile = view.findViewById<ImageView>(R.id.imageProfile)
        val textArtistName = view.findViewById<TextView>(R.id.textArtistName)
        val textArtistBio = view.findViewById<TextView>(R.id.textArtistBio)
        val imgCover = view.findViewById<ImageView>(R.id.imageCover)
        


        val sharedPref = requireContext().getSharedPreferences("NexoAppPrefs", android.content.Context.MODE_PRIVATE)
        val currentUserId = sharedPref.getLong("USER_ID", 1L)

        // Buscar dados dinâmicos do backend usando o ID real
        RetrofitClient.getApiService(requireContext()).getUserProfile(currentUserId).enqueue(object : Callback<UserBackend> {
            override fun onResponse(call: Call<UserBackend>, response: Response<UserBackend>) {
                val ctx = context
                if (!isAdded || ctx == null) return

                if (response.isSuccessful) {
                    val user = response.body()
                    user?.let {
                        currentUserInfo = it
                        textArtistName.text = it.name
                        textArtistBio.text = it.bio ?: "Sem biografia."
                        
                        view?.findViewById<TextView>(R.id.tvCountSeguidores)?.text = it.seguidoresCount.toString()
                        view?.findViewById<TextView>(R.id.tvCountCurtidas)?.text = it.curtidasCount.toString()

                        Glide.with(ctx)
                            .load(it.profileImage)
                            .placeholder(R.drawable.marcy)
                            .into(imgProfile)

                        Glide.with(ctx)
                            .load(it.coverImage)
                            .placeholder(R.drawable.shiorivolt)
                            .into(imgCover)
                    }
                } else {
                    Toast.makeText(ctx, "Erro ao carregar perfil", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserBackend>, t: Throwable) {
                val ctx = context
                if (!isAdded || ctx == null) return
                Toast.makeText(ctx, "Falha de conexão", Toast.LENGTH_SHORT).show()
            }
        })

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewProfile)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        val portfolioAdapter = PortfolioAdapter(emptyList())
        recyclerView.adapter = portfolioAdapter
        
        RetrofitClient.getApiService(requireContext()).getPosts().enqueue(object : Callback<List<com.example.nexoapp.network.PostBackend>> {
            override fun onResponse(call: Call<List<com.example.nexoapp.network.PostBackend>>, response: Response<List<com.example.nexoapp.network.PostBackend>>) {
                if (!isAdded || context == null) return
                if (response.isSuccessful) {
                    val posts = response.body() ?: emptyList()
                    // Filtra apenas as imagens dos posts do usuário atual usando safe call
                    val userPosts = posts.filter { it.artista?.id == currentUserId }
                    val imageUrls = userPosts.map { it.urlImagem }
                    portfolioAdapter.updateData(imageUrls)
                }
            }
            override fun onFailure(call: Call<List<com.example.nexoapp.network.PostBackend>>, t: Throwable) {
                val ctx = context
                if (!isAdded || ctx == null) return
                Toast.makeText(ctx, "Falha de conexão com o servidor", Toast.LENGTH_SHORT).show()
            }
        })

        val btnMensagem = view.findViewById<Button>(R.id.buttonMessage)
        btnMensagem.text = "Editar Perfil"
        btnMensagem.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }
    }
}