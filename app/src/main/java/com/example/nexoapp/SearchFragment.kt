package com.example.nexoapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var adapter: PostAdapter
    private var listaOriginal = listOf<Post>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewSearch)
        val searchEditText = view.findViewById<EditText>(R.id.editTextSearch)

        // 2 colunas e orientação vertical para o StaggeredGridLayoutManager
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        adapter = PostAdapter(emptyList())
        recyclerView.adapter = adapter

        // Consumir API
        com.example.nexoapp.network.RetrofitClient.getApiService(requireContext()).getPosts().enqueue(object : retrofit2.Callback<List<com.example.nexoapp.network.PostBackend>> {
            override fun onResponse(call: retrofit2.Call<List<com.example.nexoapp.network.PostBackend>>, response: retrofit2.Response<List<com.example.nexoapp.network.PostBackend>>) {
                if (response.isSuccessful) {
                    val postsBackend = response.body() ?: emptyList()
                    listaOriginal = postsBackend.map {
                        Post(
                            id = it.id,
                            artistName = it.artista?.name ?: "Usuário",
                            usernameTime = "@${it.artista?.name?.lowercase()?.replace(" ", "") ?: "usuario"}",
                            caption = it.descricao,
                            postImageUrl = it.urlImagem,
                            profileImageUrl = it.artista?.profileImage,
                            likesCount = it.curtidasCount
                        )
                    }
                    adapter.updateData(listaOriginal)
                }
            }
            override fun onFailure(call: retrofit2.Call<List<com.example.nexoapp.network.PostBackend>>, t: Throwable) {
                Toast.makeText(requireContext(), "Falha de conexão com o servidor", Toast.LENGTH_SHORT).show()
            }
        })

        // Filtragem local
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase()
                val listaFiltrada = listaOriginal.filter {
                    it.artistName.lowercase().contains(query) || it.caption.lowercase().contains(query)
                }
                adapter.updateData(listaFiltrada)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
}