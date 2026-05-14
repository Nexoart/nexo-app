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

class PortfolioAdapter(private val imagesList: List<Int>) : RecyclerView.Adapter<PortfolioAdapter.PortfolioViewHolder>() {

    class PortfolioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPortfolioItem: ImageView = itemView.findViewById(R.id.imgPortfolioItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PortfolioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_portfolio, parent, false)
        return PortfolioViewHolder(view)
    }

    override fun onBindViewHolder(holder: PortfolioViewHolder, position: Int) {
        val imageRes = imagesList[position]
        holder.imgPortfolioItem.setImageResource(imageRes)

        // --- A MÁGICA DO POP-UP (CARTA 1) ---
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context

            // Cria a caixa flutuante invisível
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

            // Cria uma ImageView no ar
            val imgView = ImageView(context)
            imgView.setImageResource(imageRes)
            imgView.adjustViewBounds = true

            // Coloca a imagem dentro da caixa e deixa o fundo transparente para escurecer a tela
            dialog.setContentView(imgView)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            // Quando clicar na imagem grande, ela fecha
            imgView.setOnClickListener { dialog.dismiss() }

            dialog.show()
        }
    }

    override fun getItemCount() = imagesList.size
}