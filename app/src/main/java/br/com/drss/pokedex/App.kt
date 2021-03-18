package br.com.drss.pokedex

import android.app.Application
import br.com.drss.pokedex.home.sl.applicationModule
import br.com.drss.pokedex.home.sl.networkModule
import org.koin.core.context.startKoin

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin()
    }

    private fun startKoin() {
        startKoin {
            modules(applicationModule, networkModule)
        }
    }

}