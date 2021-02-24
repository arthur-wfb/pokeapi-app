package com.example.pokeapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokeapp.databinding.FragmentMainBinding
import com.example.pokeapp.interfaces.OnPokemonClick
import com.example.pokeapp.util.FavouritePokemonsAdapter
import com.example.pokeapp.viewmodel.PokemonViewModel
import org.koin.android.ext.android.inject


class MainFragment : Fragment(), View.OnClickListener, OnPokemonClick {

    private lateinit var navController: NavController
    private val viewModel by inject<PokemonViewModel>()
    private lateinit var binding: FragmentMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentMainBinding.inflate(layoutInflater)
        viewModel.listenToFavouritePokemons()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val pokemonAdapter = FavouritePokemonsAdapter(this)

        binding.favouritePokemonsRV.layoutManager = GridLayoutManager(binding.root.context, 2)
        binding.favouritePokemonsRV.adapter = pokemonAdapter

        viewModel.favouritePokemons.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            pokemonAdapter.submitValues(it)
            binding.favouritePokemonsRV.visibility = View.VISIBLE
            binding.favPokemonsProgressBar.visibility = View.INVISIBLE
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.searchButton.setOnClickListener(this)
        binding.randomPokemonButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id){
            R.id.search_button -> navController.navigate(R.id.action_mainFragment_to_searchFragment)
            R.id.random_pokemon_button -> navController.navigate(R.id.action_mainFragment_to_pokemonDetailsFragment)
        }
    }

    override fun onPokemonClick(id: Int) {
        val bundle = bundleOf("pokemon_id" to id)
        navController.navigate(R.id.action_mainFragment_to_pokemonDetailsFragment, bundle)
    }
}