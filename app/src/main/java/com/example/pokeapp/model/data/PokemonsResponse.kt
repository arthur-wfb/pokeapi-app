package com.example.pokeapp.model.data

data class PokemonsResponse(
    val count: Int,
    val next: String,
    val previous: Any,
    val results: List<PokemonSearchResult>
)