package com.example.pokeapp.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pokeapp.R
import com.example.pokeapp.interfaces.OnPokemonClick
import com.example.pokeapp.model.data.PokemonInFavourites
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class FavouritePokemonsAdapter(private val onPokemonClick: OnPokemonClick) :
    RecyclerView.Adapter<FavouritePokemonsAdapter.MyViewHolder>() {

    private var values: ArrayList<PokemonInFavourites> = ArrayList()

    fun submitValues(newValues: ArrayList<PokemonInFavourites>) {
        values = newValues
        this.notifyDataSetChanged()
    }

    override fun getItemCount() = values.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fragment_favourite_pokemon, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.favPokemonName.text = values[position].name.capitalize()
        Picasso.get().load(values[position].spriteUrl).into(holder.favPokemonSprite)
        holder.itemView.setOnClickListener({ onPokemonClick.onPokemonClick(values[position].id) })
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var favPokemonName: TextView = itemView.findViewById(R.id.favPokemonName)
        var favPokemonSprite: ImageView = itemView.findViewById(R.id.favPokemonSprite)
    }
}