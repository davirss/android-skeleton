package br.com.drss.pokedex

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import br.com.drss.pokedex.databinding.ActivityMainBinding

class MainActivity: FragmentActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}