package com.example.pokeapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokeapp.databinding.FragmentPokemonDetailsBinding
import com.example.pokeapp.databinding.FragmentSearchBinding
import com.example.pokeapp.interfaces.OnPokemonClick
import com.example.pokeapp.model.api.NetworkState
import com.example.pokeapp.util.PokemonAdapter
import com.example.pokeapp.viewmodel.PokemonViewModel
import org.koin.android.ext.android.inject
import java.util.*


class SearchFragment : Fragment(), OnPokemonClick {

    private lateinit var navController: NavController
    private val viewModel by inject<PokemonViewModel>()
    private lateinit var binding: FragmentSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentSearchBinding.inflate(layoutInflater)

        viewModel.fetchAllPokemons()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        val pokemonAdapter = PokemonAdapter(this)

        binding.searchPokemonRV.layoutManager = LinearLayoutManager(view.context)
        binding.searchPokemonRV.adapter = pokemonAdapter
        binding.searchPokemonRV.addItemDecoration(DividerItemDecoration(binding.searchPokemonRV.context, DividerItemDecoration.VERTICAL))

        binding.submit.setOnClickListener {
            pokemonAdapter.filterByText(binding.searchInput.text.toString().toLowerCase(Locale.getDefault()))
        }

        binding.searchInput.addTextChangedListener {
            pokemonAdapter.filterByText(binding.searchInput.text.toString().toLowerCase(Locale.getDefault()))
        }

        viewModel.allPokemonsResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            pokemonAdapter.submitValues(it)
            pokemonAdapter.filterByText(binding.searchInput.text.toString().toLowerCase(Locale.getDefault()))
        })

        viewModel.networkState.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                NetworkState.SUCCESS -> {
                    binding.searchProgressBar.visibility = View.INVISIBLE
                    binding.searchPokemonRV.visibility = View.VISIBLE
                }
                NetworkState.RUNNING -> {
                    binding.searchProgressBar.visibility = View.VISIBLE
                    binding.searchPokemonRV.visibility = View.INVISIBLE
                }
            }
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        navController = Navigation.findNavController(view)
    }

    override fun onPokemonClick(id: Int) {
        val bundle = bundleOf("pokemon_id" to id)
        navController.navigate(R.id.action_searchFragment_to_pokemonDetailsFragment, bundle)
    }
}