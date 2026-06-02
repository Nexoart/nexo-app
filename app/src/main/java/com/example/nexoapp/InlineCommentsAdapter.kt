package com.example.nexoapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nexoapp.network.ComentarioBackend

class InlineCommentsAdapter(private var comments: List<ComentarioBackend>) : RecyclerView.Adapter<InlineCommentsAdapter.CommentViewHolder>() {

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvComment: TextView = itemView.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        val tv = view.findViewById<TextView>(android.R.id.text1)
        tv.setTextColor(android.graphics.Color.parseColor("#A0A0A0")) // Cinza claro para não poluir o feed
        tv.textSize = 13f 
        tv.setPadding(0, 4, 0, 4) // Menos padding
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comentario = comments[position]
        holder.tvComment.text = "${comentario.autor}: ${comentario.texto}"
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    fun updateData(newComments: List<ComentarioBackend>) {
        comments = newComments
        notifyDataSetChanged()
    }
}
