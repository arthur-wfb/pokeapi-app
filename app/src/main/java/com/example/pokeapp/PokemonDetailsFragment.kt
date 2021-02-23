package com.example.pokeapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.pokeapp.model.api.NetworkState
import com.example.pokeapp.model.data.PokemonInFavourites
import com.example.pokeapp.viewmodel.PokemonViewModel
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.inject
import kotlin.random.Random

class PokemonDetailsFragment : Fragment() {

    private val viewModel by inject<PokemonViewModel>()
    private lateinit var ref: DatabaseReference
    private  var pokemonId: Int = 0
    private var isRandomPokemonScreen = false

    private lateinit var name: TextView
    private lateinit var pokemonSprite: ImageView
    private lateinit var height: TextView
    private lateinit var weight: TextView
    private lateinit var checkBoxIsInFavourites: CheckBox
    private lateinit var randomButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var abilitiesLinearLayout: LinearLayout
    private var spriteUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        name =  view.findViewById(R.id.name)
        pokemonSprite = view.findViewById(R.id.pokemonSprite)
        height = view.findViewById(R.id.pokemonHeight)
        weight = view.findViewById(R.id.pokemonWeight)
        checkBoxIsInFavourites = view.findViewById(R.id.isInFavourites)
        randomButton = view.findViewById(R.id.randomButton)
        progressBar = view.findViewById(R.id.progressBar)
        abilitiesLinearLayout = view.findViewById(R.id.abilitiesLinearLayout)
        randomButton.visibility = if (isRandomPokemonScreen) View.VISIBLE else View.INVISIBLE

        checkBoxIsInFavourites.setOnClickListener {
            if (!checkBoxIsInFavourites.isChecked) {
                removeFromFavourite()
            } else {
                addToFavourite(PokemonInFavourites(pokemonId, name.text.toString(), spriteUrl))
            }
        }

        randomButton.setOnClickListener {
            viewModel.fetchPokemon(Random.nextInt(0, 898))
        }

        viewModel.pokemonResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { pokemon ->

            pokemonId = pokemon.id
            Log.e("pokemon sprite", pokemon.sprites.toString())
            spriteUrl = pokemon.sprites.front_default
            if (spriteUrl != null) {
                Picasso.get().load(spriteUrl).into(pokemonSprite)
            }

            name.text = pokemon.name.capitalize()
            height.text = pokemon.height.toString()
            weight.text = pokemon.weight.toString()

            abilitiesLinearLayout.removeAllViews()
            for (ability in pokemon.abilities) {
                val abilityTextView = TextView(context)
                abilityTextView.text = ability.ability.name
                abilitiesLinearLayout.addView(abilityTextView)
            }


            checkBoxIsInFavourites.isChecked = false
            viewModel.favouritePokemons.value?.forEach {
                if (it.id == pokemonId) {
                    checkBoxIsInFavourites.isChecked = true
                }
            }
        })

        viewModel.favouritePokemons.observe(viewLifecycleOwner, androidx.lifecycle.Observer { arrayList ->
            Log.e("favouritePokemons", arrayList.toString())
            arrayList.forEach {
                if (it.id == pokemonId) {
                    checkBoxIsInFavourites.isChecked = true
                }
            }
        })

        viewModel.networkState.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                NetworkState.SUCCESS -> {
                    progressBar.visibility = View.INVISIBLE
                    name.visibility = View.VISIBLE
                    pokemonSprite.visibility = View.VISIBLE
                    height.visibility = View.VISIBLE
                    weight.visibility = View.VISIBLE
                }
                NetworkState.RUNNING -> {
                    progressBar.visibility = View.VISIBLE
                    name.visibility = View.INVISIBLE
                    pokemonSprite.visibility = View.INVISIBLE
                    height.visibility = View.INVISIBLE
                    weight.visibility = View.INVISIBLE
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
            checkBoxIsInFavourites.isChecked = false
            Toast.makeText(context, "Pokemon has not been added to favorites", Toast.LENGTH_LONG).show()
        }
    }

    private fun removeFromFavourite() {
        ref.child(pokemonId.toString()).removeValue()
    }
}