package com.example.pokeapp.model.data

data class Pokemon(
    val id: Int,
    val name: String,
    val weight: Int,
    val height: Int,
    val abilities: List<Ability>,
    val sprites: Sprites
)