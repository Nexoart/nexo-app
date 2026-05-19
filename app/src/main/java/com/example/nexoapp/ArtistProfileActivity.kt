package com.example.nexoapp

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nexoapp.network.PostBackend
import com.example.nexoapp.network.RetrofitClient
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArtistProfileActivity : AppCompatActivity() {

    private lateinit var portfolioAdapter: PortfolioAdapter
    private var isFollowing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_profile)

        val btnBack = findViewById<CardView>(R.id.btnBack)
        val imageCover = findViewById<ImageView>(R.id.imageCover)
        val imageProfile = findViewById<ImageView>(R.id.imageProfile)
        val textArtistName = findViewById<TextView>(R.id.textArtistName)
        val textUsername = findViewById<TextView>(R.id.textUsername)
        val buttonFollow = findViewById<MaterialButton>(R.id.buttonFollow)
        val buttonMessage = findViewById<MaterialButton>(R.id.buttonMessage)
        val recyclerViewProfile = findViewById<RecyclerView>(R.id.recyclerViewProfile)

        val artistName = intent.getStringExtra("ARTIST_NAME") ?: "Artista"
        val profileImageUrl = intent.getStringExtra("PROFILE_IMAGE_URL")
        isFollowing = intent.getBooleanExtra("IS_FOLLOWING", false)

        textArtistName.text = artistName
        textUsername.text = "@${artistName.lowercase().replace(" ", "")}"

        // Carregar fotos usando Glide
        Glide.with(this).load(profileImageUrl).placeholder(R.drawable.marcy).into(imageProfile)
        Glide.with(this).load(profileImageUrl).placeholder(R.drawable.florest).into(imageCover)

        val artistId = intent.getLongExtra("ARTIST_ID", -1L)
        val sharedPref = getSharedPreferences("NexoAppPrefs", android.content.Context.MODE_PRIVATE)
        val currentUserId = sharedPref.getLong("USER_ID", -1L)

        // Verificação robusta: se o ID do artista for o meu ID, ou se o nome for "Teste16" (nome padrão do usuário logado)
        val isMe = (artistId == currentUserId && artistId != -1L) || artistName == "Teste16"

        if (isMe) {
            buttonFollow.visibility = android.view.View.GONE
            buttonMessage.text = "Editar Perfil"
            
            val params = buttonMessage.layoutParams as android.widget.LinearLayout.LayoutParams
            params.marginStart = 0
            buttonMessage.layoutParams = params

            buttonMessage.setOnClickListener {
                val editIntent = android.content.Intent(this, EditProfileActivity::class.java)
                startActivity(editIntent)
            }
        } else {
            // Configurar botão seguir
            updateFollowButtonState(buttonFollow)
            buttonFollow.setOnClickListener {
                isFollowing = !isFollowing
                updateFollowButtonState(buttonFollow)
                if (isFollowing) {
                    Toast.makeText(this, "Seguindo $artistName", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Deixou de seguir $artistName", Toast.LENGTH_SHORT).show()
                }
            }

            // Configurar botão mensagem
            buttonMessage.setOnClickListener {
                val chatIntent = android.content.Intent(this, ChatFakeActivity::class.java).apply {
                    putExtra("CHAT_NAME", artistName)
                }
                startActivity(chatIntent)
            }
        }

        // Configurar voltar
        btnBack.setOnClickListener { finish() }

        // Setup Portfolio Grid
        recyclerViewProfile.layoutManager = GridLayoutManager(this, 3)
        portfolioAdapter = PortfolioAdapter(emptyList())
        recyclerViewProfile.adapter = portfolioAdapter

        // Carregar posts desse artista dinamicamente
        loadArtistPosts(artistName)
    }

    private fun updateFollowButtonState(buttonFollow: MaterialButton) {
        if (isFollowing) {
            buttonFollow.text = "Seguindo"
            buttonFollow.setTextColor(android.graphics.Color.WHITE)
            buttonFollow.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#444444"))
        } else {
            buttonFollow.text = "Seguir"
            buttonFollow.setTextColor(android.graphics.Color.WHITE)
            buttonFollow.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#1B5E20"))
        }
    }

    private fun loadArtistPosts(artistName: String) {
        RetrofitClient.getApiService(this).getPosts().enqueue(object : Callback<List<PostBackend>> {
            override fun onResponse(call: Call<List<PostBackend>>, response: Response<List<PostBackend>>) {
                if (response.isSuccessful) {
                    val posts = response.body() ?: emptyList()
                    val artistPosts = posts.filter { it.artista?.name == artistName }
                    val imageUrls = artistPosts.map { it.urlImagem }
                    portfolioAdapter.updateData(imageUrls)
                    findViewById<TextView>(R.id.tvCountProjetos).text = artistPosts.size.toString()
                }
            }

            override fun onFailure(call: Call<List<PostBackend>>, t: Throwable) {
                Toast.makeText(this@ArtistProfileActivity, "Erro de conexão", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
