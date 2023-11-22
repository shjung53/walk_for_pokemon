package com.ssafy.walkforpokemon.viewmodels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.fitness.HistoryClient
import com.ssafy.walkforpokemon.data.dataclass.User
import com.ssafy.walkforpokemon.domain.health.FetchStepCountUseCase
import com.ssafy.walkforpokemon.domain.health.InitHealthClientUseCase
import com.ssafy.walkforpokemon.domain.pokemon.UpdateMainPokemonUseCase
import com.ssafy.walkforpokemon.domain.user.DrawPokemonUseCase
import com.ssafy.walkforpokemon.domain.user.FetchUserUseCase
import com.ssafy.walkforpokemon.domain.user.RegisterUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val fetchUserUseCase: FetchUserUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
    private val initHealthClientUseCase: InitHealthClientUseCase,
    private val fetchStepCountUseCase: FetchStepCountUseCase,
    private val drawPokemonUseCase: DrawPokemonUseCase,
    private val updateMainPokemonUseCase: UpdateMainPokemonUseCase,
) :
    ViewModel() {
    private var _userId = ""
        set(value) {
            if (value != "") field = value
        }
    private val userId get() = _userId

    private var _user: MutableLiveData<User> = MutableLiveData(User(""))
    val user get() = _user

    private var _myPokemonSet: MutableLiveData<MutableSet<Int>> =
        MutableLiveData(mutableSetOf<Int>())
    val myPokemonSet get() = _myPokemonSet

    private var _mainPokemon: MutableLiveData<Int> = MutableLiveData(0)

    val mainPokemon get() = _mainPokemon

    fun setUserId(idToken: String) {
        _userId = idToken
        viewModelScope.launch {
            fetchUser(userId)
        }
    }

    private suspend fun fetchUser(id: String) {
        fetchUserUseCase.invoke(id).fold(
            onSuccess = { result ->
                if (result.id.isEmpty()) {
                    registerUser(id)
                } else {
                    _user.value = result
                    if (result.myPokemons.size != myPokemonSet.value?.size) {
                        val newSet = mutableSetOf<Int>()
                        newSet.addAll(result.myPokemons)
                        _myPokemonSet.value = newSet
                    }

                    if (result.mainPokemon != mainPokemon.value) {
                        _mainPokemon.value = result.mainPokemon
                    }
                }
            },
            onFailure = { throwable ->
                Log.d("fetchUser", throwable.message.toString())
            },
        )
    }

    private fun registerUser(id: String) {
        viewModelScope.launch {
            registerUserUseCase.invoke(id).fold(
                onSuccess = {
                    Log.d("여기", "등록성공")
                },
                onFailure = {},
            )
        }
    }

    fun drawPokemon(pokemonId: Int) {
        viewModelScope.launch {
            drawPokemonUseCase.invoke(userId, pokemonId).fold(
                onSuccess = {
                    fetchUser(userId)
                },
                onFailure = {},
            )
        }
    }

    fun updateMainPokemon(pokemonId: Int) {
        viewModelScope.launch {
            user.value?.let { user ->
                val newUser = user.copy(mainPokemon = pokemonId)
                updateMainPokemonUseCase.invoke(newUser).fold(
                    onSuccess = {
                        fetchUser(user.id)
                    },
                    onFailure = {},
                )
            }
        }
    }

    fun initHealthClient(healthClient: HistoryClient) {
        viewModelScope.launch {
            initHealthClientUseCase.invoke(healthClient).fold(
                onSuccess = {},
                onFailure = {
                },
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun refreshStepCount() {
        viewModelScope.launch {
            fetchStepCountUseCase.invoke().fold(
                onSuccess = {},
                onFailure = {
                },
            )
        }
    }
}
