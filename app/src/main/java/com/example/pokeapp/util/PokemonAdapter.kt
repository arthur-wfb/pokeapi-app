package com.example.pokeapp.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pokeapp.R
import com.example.pokeapp.interfaces.OnPokemonClick
import com.example.pokeapp.model.data.PokemonSearchResult

class PokemonAdapter(private val onPokemonClick: OnPokemonClick) :
    RecyclerView.Adapter<PokemonAdapter.MyViewHolder>() {

    private var filteredValues: List<PokemonSearchResult> = emptyList()
    private var values: List<PokemonSearchResult> = emptyList()

    fun submitValues(newValues: List<PokemonSearchResult>) {
        values = newValues
        filteredValues = newValues
        this.notifyDataSetChanged()
    }

    fun filterByText(text: String) {
        filteredValues = values.filter { it.name.startsWith(text) }
        this.notifyDataSetChanged()
    }

    override fun getItemCount() = filteredValues.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fragment_search_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.pokemonNameTextView.text = filteredValues[position].name.capitalize()
        holder.itemView.setOnClickListener { onPokemonClick.onPokemonClick(getIdFromUrl(filteredValues[position].url)) }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var pokemonNameTextView: TextView = itemView.findViewById(R.id.name_in_list)
    }
}