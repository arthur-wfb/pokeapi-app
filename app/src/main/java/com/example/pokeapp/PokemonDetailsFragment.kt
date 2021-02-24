package com.example.pokeapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.pokeapp.databinding.FragmentMainBinding
import com.example.pokeapp.databinding.FragmentPokemonDetailsBinding
import com.example.pokeapp.model.api.NetworkState
import com.example.pokeapp.model.data.PokemonInFavourites
import com.example.pokeapp.viewmodel.PokemonViewModel
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.bind
import org.koin.android.ext.android.inject
import kotlin.random.Random

class PokemonDetailsFragment : Fragment() {

    private val viewModel by inject<PokemonViewModel>()
    private lateinit var ref: DatabaseReference
    private lateinit var binding: FragmentPokemonDetailsBinding

    private  var pokemonId: Int = 0
    private var isRandomPokemonScreen = false
    private var spriteUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentPokemonDetailsBinding.inflate(layoutInflater)

        ref = FirebaseDatabase.getInstance("https://pokeapp-f1dc5-default-rtdb.firebaseio.com").reference.child("favourite_pokemons")

        val idFromPreviousFragment = arguments?.getInt("pokemon_id")
        val randomPokemonId = savedInstanceState?.getInt("pokemon_id") ?: Random.nextInt(0, 898)

        isRandomPokemonScreen = idFromPreviousFragment == 0 || idFromPreviousFragment == null
        pokemonId = if (isRandomPokemonScreen) randomPokemonId else idFromPreviousFragment!!

        viewModel.fetchPokemon(pokemonId)
        viewModel.listenToFavouritePokemons()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pokemon_details, container, false)

        binding.randomButton.visibility = if (isRandomPokemonScreen) View.VISIBLE else View.INVISIBLE

        binding.isInFavourites.setOnClickListener {
            if (!binding.isInFavourites.isChecked) {
                removeFromFavourite()
            } else {
                addToFavourite(PokemonInFavourites(pokemonId, binding.name.text.toString(), spriteUrl))
            }
        }

        binding.randomButton.setOnClickListener {
            viewModel.fetchPokemon(Random.nextInt(0, 898))
        }

        viewModel.pokemonResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { pokemon ->

            pokemonId = pokemon.id
            Log.e("pokemon sprite", pokemon.sprites.toString())
            spriteUrl = pokemon.sprites.front_default
            if (spriteUrl != null) {
                Picasso.get().load(spriteUrl).into(binding.pokemonSprite)
            }

            binding.name.text = pokemon.name.capitalize()
            binding.height.text = pokemon.height.toString()
            binding.weight.text = pokemon.weight.toString()

            binding.abilitiesLinearLayout.removeAllViews()
            for (ability in pokemon.abilities) {
                val abilityTextView = TextView(context)
                abilityTextView.text = ability.ability.name
                binding.abilitiesLinearLayout.addView(abilityTextView)
            }


            binding.isInFavourites.isChecked = false
            viewModel.favouritePokemons.value?.forEach {
                if (it.id == pokemonId) {
                    binding.isInFavourites.isChecked = true
                }
            }
        })

        viewModel.favouritePokemons.observe(viewLifecycleOwner, androidx.lifecycle.Observer { arrayList ->
            Log.e("favouritePokemons", arrayList.toString())
            arrayList.forEach {
                if (it.id == pokemonId) {
                    binding.isInFavourites.isChecked = true
                }
            }
        })

        viewModel.networkState.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                NetworkState.SUCCESS -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.name.visibility = View.VISIBLE
                    binding.pokemonSprite.visibility = View.VISIBLE
                    binding.height.visibility = View.VISIBLE
                    binding.weight.visibility = View.VISIBLE
                }
                NetworkState.RUNNING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.name.visibility = View.INVISIBLE
                    binding.pokemonSprite.visibility = View.INVISIBLE
                    binding.height.visibility = View.INVISIBLE
                    binding.weight.visibility = View.INVISIBLE
                }
            }
        })

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("pokemon_id", pokemonId)
    }

    private fun addToFavourite(pokemon: PokemonInFavourites) {
        ref.child(pokemonId.toString()).setValue(pokemon).addOnCanceledListener {
            binding.isInFavourites.isChecked = false
            Toast.makeText(context, "Pokemon has not been added to favorites", Toast.LENGTH_LONG).show()
        }
    }

    private fun removeFromFavourite() {
        ref.child(pokemonId.toString()).removeValue()
    }
}