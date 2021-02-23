    package com.example.pokeapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pokeapp.model.api.NetworkState
import com.example.pokeapp.model.api.PokemonService
import com.example.pokeapp.model.data.Pokemon
import com.example.pokeapp.model.data.PokemonInFavourites
import com.example.pokeapp.model.data.PokemonSearchResult
import com.google.firebase.database.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class PokemonViewModel(private val pokemonService: PokemonService, private val databaseReference: DatabaseReference): ViewModel() {

    private var compositeDisposable = CompositeDisposable()

    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        get() = _networkState

    private val _pokemonResponse = MutableLiveData<Pokemon>()
    val pokemonResponse: LiveData<Pokemon>
        get() = _pokemonResponse

    private val _allPokemonsResponse = MutableLiveData<List<PokemonSearchResult>>()
    val allPokemonsResponse: LiveData<List<PokemonSearchResult>>
        get() = _allPokemonsResponse

    private val _favouritePokemons = MutableLiveData<ArrayList<PokemonInFavourites>>()
    val favouritePokemons: LiveData<ArrayList<PokemonInFavourites>>
        get() = _favouritePokemons

    fun fetchPokemon(id: Int) {
        _networkState.postValue(NetworkState.RUNNING)

        try {
            compositeDisposable.add(pokemonService.getPokemonById(id)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        _pokemonResponse.postValue(it)
                        _networkState.postValue(NetworkState.SUCCESS)
                    },
                    {
                        Log.e("PokeNetworkDataSource", it.message.toString())
                    }
                ))
        }

        catch (e: Exception){
            Log.e("PokeNetworkDataSource", e.message.toString())
        }
    }

    fun fetchAllPokemons() {
        _networkState.postValue(NetworkState.RUNNING)

        try {
            compositeDisposable.add(pokemonService.getAllPokemons()
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        Log.e("dd", it.toString())
                        _allPokemonsResponse.postValue(it.results)
                        _networkState.postValue(NetworkState.SUCCESS)
                    },
                    {
                        Log.e("PokeNetworkDataSource", it.message.toString())
                    }
                ))
        }

        catch (e: Exception){
            Log.e("PokeNetworkDataSource", e.message.toString())
        }
    }

    fun listenToFavouritePokemons() {
        val favouritesListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val pokemons = ArrayList<PokemonInFavourites>()

                for (pokemon in dataSnapshot.children){

                    pokemons.add(pokemon.getValue(PokemonInFavourites::class.java) as PokemonInFavourites)
                }
                _favouritePokemons.postValue(pokemons)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("load favs", "loadFavs:onCancelled", databaseError.toException())
            }
        }
        databaseReference.addValueEventListener(favouritesListener)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}