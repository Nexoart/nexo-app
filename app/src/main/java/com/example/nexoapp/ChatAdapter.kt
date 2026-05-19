package com.example.nexoapp

import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.graphics.drawable.GradientDrawable

class ChatAdapter(private val messages: List<ChatMessage>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(val container: LinearLayout, val textView: TextView) : RecyclerView.ViewHolder(container)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val context = parent.context
        val container = LinearLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.HORIZONTAL
            setPadding(16, 8, 16, 8)
        }

        val textView = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            textSize = 15f
            setTextColor(android.graphics.Color.WHITE)
            setPadding(28, 16, 28, 16)
        }
        container.addView(textView)

        return ChatViewHolder(container, textView)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        holder.textView.text = message.text

        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 24f
        }

        if (message.isFromMe) {
            holder.container.gravity = Gravity.END
            drawable.setColor(android.graphics.Color.parseColor("#1B5E20"))
            holder.textView.background = drawable
        } else {
            holder.container.gravity = Gravity.START
            drawable.setColor(android.graphics.Color.parseColor("#333333"))
            holder.textView.background = drawable
        }
    }

    override fun getItemCount() = messages.size
}
