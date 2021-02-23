package com.example.pokeapp.util

fun getIdFromUrl(url: String): Int{
    val subString = url.substring(url.length - 6)
    return subString.filter { it.isDigit() }.toInt()
}