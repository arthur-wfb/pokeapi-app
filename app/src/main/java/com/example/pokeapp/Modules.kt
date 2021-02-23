package com.example.pokeapp

import com.example.pokeapp.model.api.BASE_URL
import com.example.pokeapp.model.api.PokemonService
import com.example.pokeapp.viewmodel.PokemonViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val viewModelModule = module {
    viewModel { PokemonViewModel(get(), get()) }
}

val apiModule = module {
    single<PokemonService>{
        Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(PokemonService::class.java)
    }
}

val dataBaseModule = module {
    single<DatabaseReference>{
        FirebaseDatabase.getInstance("https://pokeapp-f1dc5-default-rtdb.firebaseio.com").reference.child("favourite_pokemons")
    }
}
