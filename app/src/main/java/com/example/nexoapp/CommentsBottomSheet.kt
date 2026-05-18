package com.example.nexoapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nexoapp.network.ComentarioBackend
import com.example.nexoapp.network.ComentarioRequest
import com.example.nexoapp.network.PostBackend
import com.example.nexoapp.network.RetrofitClient
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentsBottomSheet : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(postId: Long): CommentsBottomSheet {
            val fragment = CommentsBottomSheet()
            val args = Bundle()
            args.putLong("POST_ID", postId)
            fragment.arguments = args
            return fragment
        }
    }

    private val commentsList = mutableListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_comments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerComments)
        val etNewComment = view.findViewById<EditText>(R.id.etNewComment)
        val btnSend = view.findViewById<ImageView>(R.id.btnSendComment)

        val adapter = CommentsAdapter(commentsList)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        val postId = arguments?.getLong("POST_ID") ?: -1L
        
        // Buscar comentários reais da API
        if (postId != -1L) {
            RetrofitClient.getApiService(requireContext()).getComments(postId).enqueue(object : Callback<List<ComentarioBackend>> {
                override fun onResponse(call: Call<List<ComentarioBackend>>, response: Response<List<ComentarioBackend>>) {
                    if (response.isSuccessful) {
                        val backendComments = response.body() ?: emptyList()
                        commentsList.clear()
                        commentsList.addAll(backendComments.map { "${it.autor}: ${it.texto}" })
                        adapter.notifyDataSetChanged()
                        if (commentsList.isNotEmpty()) recycler.scrollToPosition(commentsList.size - 1)
                    }
                }
                override fun onFailure(call: Call<List<ComentarioBackend>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Falha de conexão com o servidor", Toast.LENGTH_SHORT).show()
                }
            })
        }

        btnSend.setOnClickListener {
            val text = etNewComment.text.toString()
            if (text.isNotBlank() && postId != -1L) {
                val request = ComentarioRequest(autor = "Rafilskz", texto = text)
                
                RetrofitClient.getApiService(requireContext()).addComment(postId, request).enqueue(object : Callback<PostBackend> {
                    override fun onResponse(call: Call<PostBackend>, response: Response<PostBackend>) {
                        if (response.isSuccessful) {
                            commentsList.add("Rafilskz: $text")
                            adapter.notifyItemInserted(commentsList.size - 1)
                            recycler.scrollToPosition(commentsList.size - 1)
                            etNewComment.text.clear()
                        } else {
                            Toast.makeText(requireContext(), "Erro ao enviar comentário", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<PostBackend>, t: Throwable) {
                        Toast.makeText(requireContext(), "Falha na conexão", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    inner class CommentsAdapter(private val comments: List<String>) : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {
        inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvComment = itemView.findViewById<TextView>(android.R.id.text1)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
            val tv = view.findViewById<TextView>(android.R.id.text1)
            tv.setTextColor(android.graphics.Color.WHITE)
            return CommentViewHolder(view)
        }

        override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
            holder.tvComment.text = comments[position]
        }

        override fun getItemCount() = comments.size
    }
}
