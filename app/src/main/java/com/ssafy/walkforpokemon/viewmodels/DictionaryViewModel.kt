package com.ssafy.walkforpokemon.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.ssafy.walkforpokemon.SuccessOrFailure
import com.ssafy.walkforpokemon.data.dataclass.Pokemon
import com.ssafy.walkforpokemon.domain.pokemon.InitPokemonListUseCase
import com.ssafy.walkforpokemon.util.PokemonRarityUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

private const val TAG = "DictionaryViewModel"

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

    suspend fun initPokemonList(): Result<SuccessOrFailure> {
        initPokemonListUseCase.invoke().fold(
            onSuccess = {
                Log.d(TAG, "initPokemonList() called $it")
                _pokemonList.value = it
                state["pokemonList"] = _pokemonList.value
                it.forEach { pokemon ->
                    val grade = PokemonRarityUtil.getGrade(pokemon.percentage)
                    PokemonRarityUtil.putInGradeList(pokemon.id, grade)
                }
                return Result.success(SuccessOrFailure.Success)
            },
            onFailure = {
                return Result.failure(it)
            },
        )
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
