package br.com.drss.pokedex

import android.app.Application
import br.com.drss.pokedex.di.applicationModule
import br.com.drss.pokedex.di.networkModule
import br.com.drss.pokedex.di.repositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin()
    }

    private fun startKoin() {
        startKoin {
            androidContext(applicationContext)
            modules(applicationModule, networkModule, repositoryModule)
        }
    }

}