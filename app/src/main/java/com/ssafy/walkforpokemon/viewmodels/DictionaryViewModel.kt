package com.ssafy.walkforpokemon.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.walkforpokemon.data.dataclass.Pokemon
import com.ssafy.walkforpokemon.data.dataclass.User
import com.ssafy.walkforpokemon.domain.pokemon.FetchUserPokemonListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DictionaryViewModel @Inject constructor(
    private val fetchUserPokemonListUseCase: FetchUserPokemonListUseCase,
) :
    ViewModel() {

    private var _pokemonList = MutableLiveData<List<Pokemon>>(emptyList())
    val pokemonList: LiveData<List<Pokemon>> get() = _pokemonList

    fun fetchUserPokemonList(user: User) {
        viewModelScope.launch {
            fetchUserPokemonListUseCase.invoke(user).fold(
                onSuccess = {
                    _pokemonList.value = it
                },
                onFailure = {},
            )
        }
    }
}
