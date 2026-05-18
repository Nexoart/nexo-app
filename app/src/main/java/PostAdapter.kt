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
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    fun updateData(newList: List<Post>) {
        postList = newList
        notifyDataSetChanged()
    }
}
