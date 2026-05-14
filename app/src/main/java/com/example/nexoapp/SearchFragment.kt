package com.example.nexoapp

import Post
import PostAdapter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
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

        listaOriginal = listOf(
            Post("Arte 1", "@rafilskz", "Explosão de cores", R.drawable.marcy, R.drawable.luz),
            Post("Arte 2", "@filipe_arts", "Estudo de sombras", R.drawable.anatomia, R.drawable.test),
            Post("Arte 3", "@rafilskz", "Cenário 3D", R.drawable.florest, R.drawable.eda),
            Post("Pixel Adventure", "@pixel_boy", "Pixel art retro", R.drawable.marcy, R.drawable.eda),
            Post("Cyber City", "@neo_art", "Cyberpunk vibes", R.drawable.anatomia, R.drawable.test)
        )

        adapter = PostAdapter(listaOriginal)
        recyclerView.adapter = adapter

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