package com.example.pokeapp.model.api

import com.example.pokeapp.model.data.Pokemon
import com.example.pokeapp.model.data.PokemonsResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface PokemonService {

    @GET("pokemon/{id}")
    fun getPokemonById(@Path("id") id: Int): Single<Pokemon>

    @GET("pokemon?limit=1118")
    fun getAllPokemons(): Single<PokemonsResponse>

}