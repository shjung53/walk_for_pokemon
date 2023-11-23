package com.ssafy.walkforpokemon.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.walkforpokemon.PokemonRarityUtil
import com.ssafy.walkforpokemon.data.dataclass.Pokemon
import com.ssafy.walkforpokemon.domain.pokemon.InitPokemonListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class DictionaryViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val initPokemonListUseCase: InitPokemonListUseCase,
) :
    ViewModel() {

    private var _pokemonList = MutableLiveData<List<Pokemon>>(emptyList())
    val pokemonList: LiveData<List<Pokemon>> get() = _pokemonList

    private var _myPokemonList = MutableLiveData<List<Pokemon>>(emptyList())
    val myPokemonList: LiveData<List<Pokemon>> get() = _myPokemonList

    init {
        _pokemonList.value = state["pokemonList"] ?: emptyList()
        _myPokemonList.value = state["myPokemonList"] ?: emptyList()
    }

    fun initPokemonList() {
        viewModelScope.launch {
            initPokemonListUseCase.invoke().fold(
                onSuccess = {
                    _pokemonList.value = it
                    state["pokemonList"] = _pokemonList.value
                    it.forEach { pokemon ->
                        val grade = PokemonRarityUtil.getGrade(pokemon.percentage)
                        PokemonRarityUtil.putInGradeList(pokemon.id, grade)
                    }
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
            state["myPokemonList"] = _myPokemonList.value
        }
    }

    fun getNewPokemonId(): Int {
        val grade = PokemonRarityUtil.drawGrade()
        val gradeList = PokemonRarityUtil.getList(grade)
        val listSize = gradeList.size

        val selectedItemIndex = (Random.nextDouble() * listSize).toInt()

        return gradeList[selectedItemIndex]
    }
}
