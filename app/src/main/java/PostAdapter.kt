import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView // <-- Já importei o ImageView para você
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nexoapp.R

// 1. O "Molde" atualizado com o espaço para a imagem
data class Post(
    val artistName: String,
    val usernameTime: String,
    val caption: String,
    val postImageResId: Int, // <-- A variável da imagem (Resource ID) já está aqui
    val profileImageResId: Int
)



class PostAdapter(private val postList: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvArtistName: TextView = itemView.findViewById(R.id.tvArtistName)
        val tvUsernameTime: TextView = itemView.findViewById(R.id.tvUsernameTime)
        val tvPostCaption: TextView = itemView.findViewById(R.id.tvPostCaption)

        // TODO 1: Declare a variável 'imgPost' aqui (copie a lógica das linhas de cima).
        val imgPost: ImageView = itemView.findViewById(R.id.imgPost)
        // Lembre-se: o tipo dela não é TextView, é ImageView, e o ID no XML é R.id.imgPost
        val imgProfile: ImageView = itemView.findViewById(R.id.imgProfile)
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


        //  Para imagens, não usamos o '.text =', nós usamos o comando '.setImageResource(  )'
        holder.imgPost.setImageResource(currentPost.postImageResId)

        holder.imgProfile.setImageResource(currentPost.profileImageResId)
    }

    override fun getItemCount(): Int {
        return postList.size
    }
}