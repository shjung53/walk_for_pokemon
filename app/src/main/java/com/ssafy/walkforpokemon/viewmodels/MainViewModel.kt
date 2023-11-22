package com.ssafy.walkforpokemon.viewmodels

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.walkforpokemon.domain.health.AddMileageUseCase
import com.ssafy.walkforpokemon.domain.health.FetchStepCountUseCase
import com.ssafy.walkforpokemon.domain.pokemon.UpdateMainPokemonUseCase
import com.ssafy.walkforpokemon.domain.user.DrawPokemonUseCase
import com.ssafy.walkforpokemon.domain.user.FetchUserUseCase
import com.ssafy.walkforpokemon.domain.user.RegisterUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MainViewModel"

@HiltViewModel
class MainViewModel @Inject constructor(
    private val fetchUserUseCase: FetchUserUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
    private val fetchStepCountUseCase: FetchStepCountUseCase,
    private val drawPokemonUseCase: DrawPokemonUseCase,
    private val updateMainPokemonUseCase: UpdateMainPokemonUseCase,
    private val addMileageUseCase: AddMileageUseCase
) :
    ViewModel() {
    private var _userId = ""
        set(value) {
            if (value != "") field = value
        }
    val userId get() = _userId

    private var _myPokemonSet: MutableLiveData<MutableSet<Int>> =
        MutableLiveData(mutableSetOf<Int>())
    val myPokemonSet get() = _myPokemonSet

    private var _mainPokemon: MutableLiveData<Int> = MutableLiveData(0)
    val mainPokemon get() = _mainPokemon

    private var _addedMileage: MutableLiveData<Int> = MutableLiveData(0)
    val addedMileage get() = _addedMileage

    private var _currentMileage: MutableLiveData<Int> = MutableLiveData(0)
    val currentMileage get() = _currentMileage

    private var _stepCount: MutableLiveData<Int> = MutableLiveData(0)
    val stepCount get() = _stepCount

    @RequiresApi(Build.VERSION_CODES.O)
    fun setUserId(context: Context, idToken: String) {
        _userId = idToken
        initUser(context, userId)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initUser(context: Context, id: String) {
        viewModelScope.launch {
            fetchUserUseCase.invoke(id).fold(
                onSuccess = { result ->
                    if (result.id.isEmpty()) {
                        registerUser(id)
                    } else {
                        val newSet = mutableSetOf<Int>()
                        newSet.addAll(result.myPokemons)
                        _myPokemonSet.value = newSet

                        _mainPokemon.value = result.mainPokemon

                        _currentMileage.value = result.currentMileage

                        _addedMileage.value = result.addedMileage
                    }
                },
                onFailure = { throwable ->
                    Log.d(TAG, "initUser() called with: throwable = $throwable")
                },
            )
        }
        viewModelScope.launch {
            refreshStepCount(context)
        }
    }

    private fun registerUser(id: String) {
        viewModelScope.launch {
            registerUserUseCase.invoke(id).fold(
                onSuccess = { _myPokemonSet.value = mutableSetOf() },
                onFailure = {},
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun drawNewPokemon(newPokemonId: Int) {
        viewModelScope.launch {
            drawPokemonUseCase.invoke(userId, newPokemonId).fold(
                onSuccess = {
                    val newPokemonSet = mutableSetOf<Int>()
                    myPokemonSet.value?.let {
                        newPokemonSet.addAll(it)
                    }
                    newPokemonSet.add(newPokemonId)
                    _myPokemonSet.value = newPokemonSet
                },
                onFailure = {},
            )
        }
    }

    fun updateMainPokemon(pokemonId: Int) {
        viewModelScope.launch {
            updateMainPokemonUseCase.invoke(userId, pokemonId).fold(
                onSuccess = {
                    _mainPokemon.value = pokemonId
                },
                onFailure = {},
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun refreshStepCount(context: Context) {
        viewModelScope.launch {
            fetchStepCountUseCase.invoke(context).fold(
                onSuccess = { _stepCount.value = it },
                onFailure = {},
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateStepCountToAdd(newStepCount: Int) {
        if ((addedMileage.value ?: 0) < newStepCount) {
            Log.d(
                TAG,
                "calculateStepCountToAdd() called with: newStepCount = $newStepCount, ${addedMileage.value ?: 0}"
            )
            val mileageToAdd = newStepCount - (addedMileage.value ?: 0)
            addMileageToUser(mileageToAdd)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addMileageToUser(mileageToAdd: Int) {
        val newCurrentMileage = (currentMileage.value ?: 0) + mileageToAdd
        viewModelScope.launch {
            addMileageUseCase.invoke(userId, newCurrentMileage).fold(
                onSuccess = {
                    _currentMileage.value = newCurrentMileage
                    _addedMileage.value = _addedMileage.value?.plus(mileageToAdd)
                },
                onFailure = {}
            )
        }
    }
}
