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
import com.example.pokeapp.interfaces.OnPokemonClick
import com.example.pokeapp.model.api.NetworkState
import com.example.pokeapp.util.PokemonAdapter
import com.example.pokeapp.viewmodel.PokemonViewModel
import org.koin.android.ext.android.inject
import java.util.*


class SearchFragment : Fragment(), OnPokemonClick {

    private lateinit var navController: NavController
    private val viewModel by inject<PokemonViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.fetchAllPokemons()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.searchPokemonRV)
        val pokemonAdapter = PokemonAdapter(this)
        val searchInput = view.findViewById<EditText>(R.id.searchInput)
        val submitButton = view.findViewById<Button>(R.id.submit)
        val progressBar = view.findViewById<ProgressBar>(R.id.searchProgressBar)

        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.adapter = pokemonAdapter
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))

        submitButton.setOnClickListener {
            pokemonAdapter.filterByText(searchInput.text.toString().toLowerCase(Locale.getDefault()))
        }

        searchInput.addTextChangedListener {
            pokemonAdapter.filterByText(searchInput.text.toString().toLowerCase(Locale.getDefault()))
        }

        viewModel.allPokemonsResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            pokemonAdapter.submitValues(it)
            pokemonAdapter.filterByText(searchInput.text.toString().toLowerCase(Locale.getDefault()))
        })

        viewModel.networkState.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                NetworkState.SUCCESS -> {
                    progressBar.visibility = View.INVISIBLE
                    recyclerView.visibility = View.VISIBLE
                }
                NetworkState.RUNNING -> {
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.INVISIBLE
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