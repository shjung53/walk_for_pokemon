package com.ssafy.walkforpokemon.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.walkforpokemon.data.dataclass.Pokemon
import com.ssafy.walkforpokemon.data.dataclass.User
import com.ssafy.walkforpokemon.domain.pokemon.InitPokemonListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DictionaryViewModel @Inject constructor(
    private val initPokemonListUseCase: InitPokemonListUseCase,
) :
    ViewModel() {

    private var _pokemonList = MutableLiveData<List<Pokemon>>(emptyList())
    val pokemonList: LiveData<List<Pokemon>> get() = _pokemonList

    private var _myPokemonList = MutableLiveData<List<Pokemon>>(emptyList())
    val myPokemonList: LiveData<List<Pokemon>> get() = _myPokemonList

    fun initPokemonList() {
        viewModelScope.launch {
            initPokemonListUseCase.invoke().fold(
                onSuccess = {
                    _pokemonList.value = it
                },
                onFailure = {},
            )
        }
    }

    fun updateUserPokemonList(mainPokemonId: Int, userPokemonSet: MutableSet<Int>) {
        pokemonList.value?.let {
            val newPokemonList = it.map { pokemon ->
                pokemon.copy(
                    isActive = userPokemonSet.contains(pokemon.id),
                    isMain = pokemon.id == mainPokemonId,
                )
            }
            _myPokemonList.value = newPokemonList
        }
    }
}
