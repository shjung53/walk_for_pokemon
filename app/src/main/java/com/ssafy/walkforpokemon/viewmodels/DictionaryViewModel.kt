package com.ssafy.walkforpokemon.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ssafy.walkforpokemon.data.datasource.PokemonDataSource
import com.ssafy.walkforpokemon.data.repository.PokemonRepository
import kotlinx.coroutines.launch

class DictionaryViewModel(private val pokemonRepository: PokemonRepository) : ViewModel() {

    fun fetchPokemons(){
        viewModelScope.launch {
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                DictionaryViewModel(
                    pokemonRepository = PokemonRepository(
                        pokemonDataSource = PokemonDataSource(
                        ),
                    ),
                )
            }
        }
    }
}
