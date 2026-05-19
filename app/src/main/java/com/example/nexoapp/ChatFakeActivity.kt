package com.example.nexoapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class ChatFakeActivity : AppCompatActivity() {

    private val chatMessages = mutableListOf<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var rvChat: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_fake)

        val tvChatName = findViewById<TextView>(R.id.tvChatName)
        rvChat = findViewById(R.id.rvChat)
        val editChatMessage = findViewById<EditText>(R.id.editChatMessage)
        val btnSendChatMessage = findViewById<MaterialButton>(R.id.btnSendChatMessage)

        val chatName = intent.getStringExtra("CHAT_NAME") ?: "Conversa"
        tvChatName.text = chatName

        // Mensagem inicial de exemplo
        chatMessages.add(ChatMessage("Olá! Vi seu portfólio no NexoArt. Você está aceitando comissões?", false))

        chatAdapter = ChatAdapter(chatMessages)
        rvChat.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        rvChat.adapter = chatAdapter

        btnSendChatMessage.setOnClickListener {
            val text = editChatMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                // A) Adicionar a mensagem enviada à lista
                chatMessages.add(ChatMessage(text, true))
                // B) Limpar o EditText
                editChatMessage.setText("")
                // C) Atualizar o adapter e fazer scroll para o final
                chatAdapter.notifyItemInserted(chatMessages.size - 1)
                rvChat.scrollToPosition(chatMessages.size - 1)

                // D) O Truque: Resposta simulada após 1.5s
                Handler(Looper.getMainLooper()).postDelayed({
                    chatMessages.add(ChatMessage("Que arte incrível! A anatomia ficou perfeita, curti muito a ideia.", false))
                    chatAdapter.notifyItemInserted(chatMessages.size - 1)
                    rvChat.scrollToPosition(chatMessages.size - 1)
                }, 1500)
            }
        }
    }
}