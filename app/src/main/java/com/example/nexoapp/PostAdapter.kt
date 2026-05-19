package com.example.nexoapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nexoapp.R
import com.example.nexoapp.network.RetrofitClient
import com.example.nexoapp.network.PostBackend
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.emptyList

data class Post(
    val id: Long?,
    val artistName: String,
    val usernameTime: String,
    val caption: String,
    val postImageUrl: String?, // Atualizado para suportar URL da Cloudinary
    val profileImageUrl: String?, // Substituiu profileImageResId
    var likesCount: Int = 0, // Counter dinâmico vindo da API
    var isLiked: Boolean = false,
    var isFollowing: Boolean = false
)

class PostAdapter(private var postList: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvArtistName: TextView = itemView.findViewById(R.id.tvArtistName)
        val tvUsernameTime: TextView = itemView.findViewById(R.id.tvUsernameTime)
        val tvPostCaption: TextView = itemView.findViewById(R.id.tvPostCaption)
        val imgPost: ImageView = itemView.findViewById(R.id.imgPost)
        val imgProfile: ImageView = itemView.findViewById(R.id.imgProfile)
        val btnFollow: TextView = itemView.findViewById(R.id.btnFollow)
        val btnComment: android.widget.LinearLayout = itemView.findViewById(R.id.btnComment)
        val tvLikeCount: TextView = itemView.findViewById(R.id.tvLikeCount)
        val btnLike: ImageView = itemView.findViewById(R.id.btnLike)
        
        // Inline Comments
        val rvInlineComments: RecyclerView = itemView.findViewById(R.id.rvInlineComments)
        val editInlineComment: android.widget.EditText = itemView.findViewById(R.id.editInlineComment)
        val btnSendInlineComment: ImageView = itemView.findViewById(R.id.btnSendInlineComment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val currentPost = postList[position]

        holder.tvArtistName.text = currentPost.artistName
        holder.tvUsernameTime.text = currentPost.usernameTime
        holder.tvPostCaption.text = currentPost.caption
        
        // Renderizar a imagem usando Glide (com placeholder padrão se a URL falhar ou for vazia)
        com.bumptech.glide.Glide.with(holder.itemView.context)
            .load(currentPost.postImageUrl)
            .placeholder(R.drawable.marcy)
            .into(holder.imgPost)
            
        // Foto de perfil usando Glide
        com.bumptech.glide.Glide.with(holder.itemView.context)
            .load(currentPost.profileImageUrl)
            .placeholder(R.drawable.marcy)
            .into(holder.imgProfile)

        holder.tvLikeCount.text = currentPost.likesCount.toString()

        // Clique no nome ou foto do artista para ver o perfil completo
        val profileClickListener = View.OnClickListener {
            val context = holder.itemView.context
            val intent = android.content.Intent(context, com.example.nexoapp.ArtistProfileActivity::class.java).apply {
                putExtra("ARTIST_NAME", currentPost.artistName)
                putExtra("PROFILE_IMAGE_URL", currentPost.profileImageUrl)
                putExtra("IS_FOLLOWING", currentPost.isFollowing)
            }
            context.startActivity(intent)
        }
        holder.tvArtistName.setOnClickListener(profileClickListener)
        holder.imgProfile.setOnClickListener(profileClickListener)

        // Botão de Seguir
        if (currentPost.isFollowing) {
            holder.btnFollow.text = "Seguindo"
            holder.btnFollow.setTextColor(android.graphics.Color.WHITE)
            holder.btnFollow.setBackgroundResource(R.drawable.bg_tag_selected)
        } else {
            holder.btnFollow.text = "Seguir"
            holder.btnFollow.setTextColor(android.graphics.Color.parseColor("#1B5E20"))
            holder.btnFollow.setBackgroundResource(R.drawable.bg_tag_unselected)
        }

        holder.btnFollow.setOnClickListener {
            val currentPos = holder.adapterPosition
            if (currentPos != RecyclerView.NO_POSITION) {
                currentPost.isFollowing = !currentPost.isFollowing
                notifyItemChanged(currentPos)
            }
        }

        // Abrir Comentários
        holder.btnComment.setOnClickListener {
            val postId = currentPost.id
            if (postId != null) {
                val activity = holder.itemView.context as? androidx.appcompat.app.AppCompatActivity
                activity?.let {
                    val bottomSheet = com.example.nexoapp.CommentsBottomSheet.newInstance(postId)
                    bottomSheet.show(it.supportFragmentManager, "CommentsBottomSheet")
                }
            }
        }

        // 1. O Kotlin pergunta para a memória: "Esse post tem like?" e desenha o ícone
        if (currentPost.isLiked) {
            holder.btnLike.setImageResource(R.drawable.ic_favorite) // Coração preenchido
            holder.btnLike.setColorFilter(android.graphics.Color.parseColor("#1B5E20")) // Verde do Nexo
        } else {
            holder.btnLike.setImageResource(R.drawable.ic_favorite_border) // Coração vazio
            holder.btnLike.setColorFilter(android.graphics.Color.parseColor("#FFFFFF")) // Branco
        }

        // 2. O Kotlin fica escutando o clique do dedo do usuário no botão (Optimistic UI)
        holder.btnLike.setOnClickListener {
            val currentPos = holder.adapterPosition
            val postId = currentPost.id
            
            if (currentPos != RecyclerView.NO_POSITION && postId != null) {
                // Guardar estado original para caso a API falhe
                val originalLikeState = currentPost.isLiked
                val originalLikesCount = currentPost.likesCount

                // Atualiza a UI imediatamente para o usuário (Otimista)
                currentPost.isLiked = !currentPost.isLiked
                if (currentPost.isLiked) currentPost.likesCount++ else currentPost.likesCount--
                notifyItemChanged(currentPos)

                // Faz a chamada Retrofit em background
                RetrofitClient.getApiService(holder.itemView.context).likePost(postId).enqueue(object : Callback<PostBackend> {
                    override fun onResponse(call: Call<PostBackend>, response: Response<PostBackend>) {
                        if (!response.isSuccessful) {
                            // Reverte se o backend der erro (Ex: 500)
                            currentPost.isLiked = originalLikeState
                            currentPost.likesCount = originalLikesCount
                            notifyItemChanged(currentPos)
                        }
                    }

                    override fun onFailure(call: Call<PostBackend>, t: Throwable) {
                        // Reverte se não houver internet ou falha de conexão
                        currentPost.isLiked = originalLikeState
                        currentPost.likesCount = originalLikesCount
                        notifyItemChanged(currentPos)
                    }
                })
            }
        }

        // Configurar lista aninhada de comentários
        holder.rvInlineComments.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(holder.itemView.context)
        val commentsAdapter = com.example.nexoapp.InlineCommentsAdapter(emptyList()) 
        holder.rvInlineComments.adapter = commentsAdapter

        // Carregar comentários daquele post
        val postId = currentPost.id
        if (postId != null) {
            RetrofitClient.getApiService(holder.itemView.context).getComments(postId).enqueue(object : Callback<List<com.example.nexoapp.network.ComentarioBackend>> {
                override fun onResponse(call: Call<List<com.example.nexoapp.network.ComentarioBackend>>, response: Response<List<com.example.nexoapp.network.ComentarioBackend>>) {
                    if (response.isSuccessful) {
                        commentsAdapter.updateData(response.body() ?: emptyList())
                    }
                }
                override fun onFailure(call: Call<List<com.example.nexoapp.network.ComentarioBackend>>, t: Throwable) {}
            })
        }

        // Enviar comentário inline
        holder.btnSendInlineComment.setOnClickListener {
            val text = holder.editInlineComment.text.toString()
            if (text.isNotBlank() && postId != null) {
                // Enviar com autor "Usuário" (o ideal é buscar o nome no SharedPreferences se necessário)
                val req = com.example.nexoapp.network.ComentarioRequest(autor = "Usuário", texto = text)
                
                RetrofitClient.getApiService(holder.itemView.context).addComment(postId, req).enqueue(object : Callback<PostBackend> {
                    override fun onResponse(call: Call<PostBackend>, response: Response<PostBackend>) {
                        if (response.isSuccessful) {
                            holder.editInlineComment.text.clear()
                            
                            // Recarregar a lista de comentários para atualizar a UI
                            RetrofitClient.getApiService(holder.itemView.context).getComments(postId).enqueue(object : Callback<List<com.example.nexoapp.network.ComentarioBackend>> {
                                override fun onResponse(c: Call<List<com.example.nexoapp.network.ComentarioBackend>>, r: Response<List<com.example.nexoapp.network.ComentarioBackend>>) {
                                    if (r.isSuccessful) {
                                        commentsAdapter.updateData(r.body() ?: emptyList())
                                    }
                                }
                                override fun onFailure(c: Call<List<com.example.nexoapp.network.ComentarioBackend>>, t: Throwable) {}
                            })
                        }
                    }
                    override fun onFailure(call: Call<PostBackend>, t: Throwable) {}
                })
            }
        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    fun updateData(newList: List<Post>) {
        postList = newList
        notifyDataSetChanged()
    }
}
