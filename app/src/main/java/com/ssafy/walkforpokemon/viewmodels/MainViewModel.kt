package com.ssafy.walkforpokemon.viewmodels

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.ssafy.walkforpokemon.SuccessOrFailure
import com.ssafy.walkforpokemon.domain.health.AddMileageUseCase
import com.ssafy.walkforpokemon.domain.health.FetchStepCountUseCase
import com.ssafy.walkforpokemon.domain.health.UseMileageUseCase
import com.ssafy.walkforpokemon.domain.pokemon.UpdateMainPokemonUseCase
import com.ssafy.walkforpokemon.domain.user.DrawPokemonUseCase
import com.ssafy.walkforpokemon.domain.user.FetchUserUseCase
import com.ssafy.walkforpokemon.domain.user.RegisterUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TAG = "MainViewModel"

@HiltViewModel
class MainViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val fetchUserUseCase: FetchUserUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
    private val fetchStepCountUseCase: FetchStepCountUseCase,
    private val drawPokemonUseCase: DrawPokemonUseCase,
    private val updateMainPokemonUseCase: UpdateMainPokemonUseCase,
    private val useMileageUseCase: UseMileageUseCase,
    private val addMileageUseCase: AddMileageUseCase,
) : ViewModel() {
    private var _userId = ""
        set(value) {
            if (value != "") {
                field = value
                state["userId"] = value
            }
        }
    val userId get() = _userId

    private var _myPokemonSet: MutableLiveData<MutableSet<Int>> =
        MutableLiveData(mutableSetOf<Int>())
    val myPokemonSet get() = _myPokemonSet

    private var _mainPokemon: MutableLiveData<Int> = MutableLiveData(0)
    val mainPokemon get() = _mainPokemon

    private var _addedMileage: MutableLiveData<Int> = MutableLiveData(0)
    private val addedMileage get() = _addedMileage

    private var _currentMileage: MutableLiveData<Int> = MutableLiveData(0)
    val currentMileage get() = _currentMileage

    private var _stepCount: MutableLiveData<Int> = MutableLiveData(0)
    val stepCount get() = _stepCount

    init {
        _userId = state.get<String>("userId") ?: ""
        _myPokemonSet.value = state["myPokemonSet"] ?: mutableSetOf()
        _mainPokemon.value = state["mainPokemon"] ?: 0
        _addedMileage.value = state["addedMileage"] ?: 0
        _currentMileage.value = state["currentMileage"] ?: 0
        _stepCount.value = state["stepCount"] ?: 0
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun setUserId(idToken: String): Result<SuccessOrFailure> {
        _userId = idToken
        initUser(userId)
            .fold(
                onSuccess = { return Result.success(it) },
                onFailure = { return Result.failure(it) },
            )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun initUser(id: String): Result<SuccessOrFailure> {
        fetchUserUseCase.invoke(id).fold(
            onSuccess = { result ->
                if (result.id == "null" || result.id.isEmpty()) {
                    registerUser(id).fold(
                        onSuccess = { return Result.success(it) },
                        onFailure = { return Result.failure(it) },
                    )
                } else {
                    val newSet = mutableSetOf<Int>()
                    newSet.addAll(result.myPokemons)
                    Log.d(TAG, "initUser() called with: result = $result")
                    _myPokemonSet.value = newSet
                    _mainPokemon.value = result.mainPokemon
                    _currentMileage.value = result.currentMileage
                    _addedMileage.value = result.addedMileage
                    state["myPokemonSet"] = _myPokemonSet.value
                    state["mainPokemon"] = _mainPokemon.value
                    state["currentMileage"] = _currentMileage.value
                    state["addedMileage"] = _addedMileage.value
                }
                return Result.success(SuccessOrFailure.Success)
            },
            onFailure = { throwable ->
                Log.d(TAG, "initUser() called with: throwable = $throwable")
                return Result.failure(throwable)
            },
        )
    }

    private suspend fun registerUser(id: String): Result<SuccessOrFailure> {
        registerUserUseCase.invoke(id).fold(
            onSuccess = { return Result.success(it) },
            onFailure = { return Result.failure(it) },
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun drawPokemon(newPokemonId: Int, duplication: Boolean): Result<SuccessOrFailure> {
        var cost = 1000
        if (duplication) cost = 800
        val newCurrentMileage = (currentMileage.value ?: 0) - cost
        useMileageUseCase.invoke(userId, newCurrentMileage).fold(
            onSuccess = {
                _currentMileage.value = newCurrentMileage
                state["currentMileage"] = _currentMileage.value
                addNewPokemon(newPokemonId).fold(
                    onSuccess = { return Result.success(it) },
                    onFailure = { return Result.failure(it) },
                )
            },
            onFailure = { return Result.failure(it) },
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun addNewPokemon(newPokemonId: Int): Result<SuccessOrFailure> {
        drawPokemonUseCase.invoke(userId, newPokemonId).fold(
            onSuccess = {
                val newPokemonSet = mutableSetOf<Int>()
                myPokemonSet.value?.let {
                    newPokemonSet.addAll(it)
                }
                newPokemonSet.add(newPokemonId)
                _myPokemonSet.value = newPokemonSet
                state["myPokemonSet"] = _myPokemonSet.value
                return Result.success(it)
            },
            onFailure = { return Result.failure(it) },
        )
    }

    suspend fun updateMainPokemon(pokemonId: Int): Result<SuccessOrFailure> {
        updateMainPokemonUseCase.invoke(userId, pokemonId).fold(
            onSuccess = {
                _mainPokemon.value = pokemonId
                state["mainPokemon"] = _mainPokemon.value
                return Result.success(it)
            },
            onFailure = { return Result.failure(it) },
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun refreshStepCount(context: Context): Result<SuccessOrFailure> {
        fetchStepCountUseCase.invoke(context).fold(
            onSuccess = {
                _stepCount.value = it
                state["stepCount"] = _stepCount.value
                return Result.success(SuccessOrFailure.Success)
            },
            onFailure = {
                return Result.failure(it)
            },
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun calculateStepCountToAdd(newStepCount: Int): Result<SuccessOrFailure> {
        if ((addedMileage.value ?: 0) < newStepCount) {
            val mileageToAdd = newStepCount - (addedMileage.value ?: 0)
            addMileageToUser(mileageToAdd).fold(
                onSuccess = { return Result.success(it) },
                onFailure = { return Result.failure(it) },
            )
        }
        return Result.success(SuccessOrFailure.Success)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun addMileageToUser(mileageToAdd: Int): Result<SuccessOrFailure> {
        val newCurrentMileage = (currentMileage.value ?: 0) + mileageToAdd
        val newAddedMileage = (addedMileage.value ?: 0) + mileageToAdd
        addMileageUseCase.invoke(userId, newCurrentMileage, newAddedMileage).fold(
            onSuccess = {
                _currentMileage.value = newCurrentMileage
                _addedMileage.value = newAddedMileage
                state["currentMileage"] = _currentMileage.value
                state["addedMileage"] = _addedMileage.value
                return Result.success(it)
            },
            onFailure = {
                return Result.failure(it)
            },
        )
    }
}
