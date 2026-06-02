package com.example.nexoapp

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PortfolioAdapter(private var imagesList: List<String?>) : RecyclerView.Adapter<PortfolioAdapter.PortfolioViewHolder>() {

    class PortfolioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPortfolioItem: ImageView = itemView.findViewById(R.id.imgPortfolioItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PortfolioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_portfolio, parent, false)
        return PortfolioViewHolder(view)
    }

    override fun onBindViewHolder(holder: PortfolioViewHolder, position: Int) {
        val imageUrl = imagesList[position]
        Glide.with(holder.itemView.context).load(imageUrl).placeholder(R.drawable.marcy).into(holder.imgPortfolioItem)

        // --- A MÁGICA DO POP-UP (CARTA 1) ---
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context

            // Cria a caixa flutuante invisível
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

            // Cria uma ImageView no ar com parâmetros de tamanho explícitos
            val imgView = ImageView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.FIT_CENTER
                adjustViewBounds = true
            }

            // Carrega a imagem com Glide usando placeholder
            Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.marcy)
                .into(imgView)

            // Coloca a imagem dentro da caixa
            dialog.setContentView(imgView)
            
            // Deixa o fundo transparente para escurecer a tela de forma elegante
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.argb(200, 0, 0, 0)))
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

            // Quando clicar na imagem grande, ela fecha
            imgView.setOnClickListener { dialog.dismiss() }

            dialog.show()
        }
    }

    override fun getItemCount() = imagesList.size
    
    fun updateData(newList: List<String?>) {
        imagesList = newList
        notifyDataSetChanged()
    }
}