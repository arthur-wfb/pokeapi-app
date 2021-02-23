package com.example.pokeapp

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PokeApp: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@PokeApp)
            modules(listOf(viewModelModule, apiModule, dataBaseModule))
        }
    }
}