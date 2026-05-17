import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nexoapp.R

data class Post(
    val artistName: String,
    val usernameTime: String,
    val caption: String,
    val postImageUrl: String?, // Atualizado para suportar URL da Cloudinary
    val profileImageResId: Int,
    var isLiked: Boolean = false // <-- PASSO 1: A variável de memória que guarda se o post tem like
)

class PostAdapter(private var postList: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvArtistName: TextView = itemView.findViewById(R.id.tvArtistName)
        val tvUsernameTime: TextView = itemView.findViewById(R.id.tvUsernameTime)
        val tvPostCaption: TextView = itemView.findViewById(R.id.tvPostCaption)
        val imgPost: ImageView = itemView.findViewById(R.id.imgPost)
        val imgProfile: ImageView = itemView.findViewById(R.id.imgProfile)

        // PASSO 3 (Parte A): Declarando o botão de Like para o Kotlin encontrar no layout
        // ATENÇÃO: Confirme se o ID do coração no seu item_post.xml é realmente btnLike!
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
            
        holder.imgProfile.setImageResource(currentPost.profileImageResId)

        // --- PASSO 3 (Parte B): LÓGICA DO BOTÃO DE LIKE ---

        // 1. O Kotlin pergunta para a memória: "Esse post tem like?" e desenha o ícone
        if (currentPost.isLiked) {
            holder.btnLike.setImageResource(R.drawable.ic_favorite) // Coração preenchido
            holder.btnLike.setColorFilter(android.graphics.Color.parseColor("#1B5E20")) // Verde do Nexo
        } else {
            holder.btnLike.setImageResource(R.drawable.ic_favorite_border) // Coração vazio
            holder.btnLike.setColorFilter(android.graphics.Color.parseColor("#FFFFFF")) // Branco
        }

        // 2. O Kotlin fica escutando o clique do dedo do usuário no botão
        holder.btnLike.setOnClickListener {
            // Inverte a memória (se era falso vira verdadeiro, e vice-versa)
            currentPost.isLiked = !currentPost.isLiked

            // Avisa o Android: "O item desta posição mudou! Desenhe-o novamente!"
            notifyItemChanged(position)
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