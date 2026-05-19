package com.example.nexoapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Conversation(val name: String, val lastMessage: String, val imageResId: Int)

class MessageAdapter(private val conversations: List<Conversation>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvLastMessage: TextView = itemView.findViewById(R.id.tvLastMessage)
        val imgProfile: ImageView = itemView.findViewById(R.id.imgProfile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val conv = conversations[position]
        holder.tvName.text = conv.name
        holder.tvLastMessage.text = conv.lastMessage
        holder.imgProfile.setImageResource(conv.imageResId)

        holder.itemView.setOnClickListener {
            val intent = android.content.Intent(it.context, ChatFakeActivity::class.java)
            intent.putExtra("CHAT_NAME", conv.name)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount() = conversations.size
}
