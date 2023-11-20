package com.ssafy.walkforpokemon.viewmodels

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.fitness.HistoryClient
import com.ssafy.walkforpokemon.data.dataclass.User
import com.ssafy.walkforpokemon.domain.health.FetchStepCountUseCase
import com.ssafy.walkforpokemon.domain.health.InitHealthClientUseCase
import com.ssafy.walkforpokemon.domain.user.FetchUserIdUseCase
import com.ssafy.walkforpokemon.domain.user.FetchUserUseCase
import com.ssafy.walkforpokemon.domain.user.RegisterUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val fetchUserIdUseCase: FetchUserIdUseCase,
    private val fetchUserUseCase: FetchUserUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
    private val initHealthClientUseCase: InitHealthClientUseCase,
    private val fetchStepCountUseCase: FetchStepCountUseCase,
) :
    ViewModel() {

    private var _user: MutableLiveData<User> = MutableLiveData()
    private val user get() = _user

    init {
        _user.value = User("")
    }

    private suspend fun fetchUserId() {
        var id = ""
        withContext(Dispatchers.IO) {
            fetchUserIdUseCase.invoke().fold(
                onSuccess = { id = it },
                onFailure = {}
            )
        }
        val temp = _user.value?.copy()
        _user.value = temp?.copy(id = id)
    }

    fun fetchUser() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                fetchUserId()
                user.value?.let {
                    fetchUserUseCase.invoke(it).fold(
                        onSuccess = { result ->
                            if (result.id == "") {
                                registerUser()
                            } else {
                                _user.value = result
                            }
                        },
                        onFailure = { throwable ->
                            Log.d("fetchUser", throwable.message.toString())
                        }
                    )
                }

            }
        }
    }

    private fun registerUser() {
        viewModelScope.launch {
            user.value?.let {
                registerUserUseCase.invoke(it).fold(
                    onSuccess = {},
                    onFailure = {
                    }
                )
            }
        }
    }

    fun initHealthClient(healthClient: HistoryClient) {
        viewModelScope.launch {
            initHealthClientUseCase.invoke(healthClient).fold(
                onSuccess = {},
                onFailure = {
                }
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchStepCount() {
        viewModelScope.launch {
            fetchStepCountUseCase.invoke().fold(
                onSuccess = {},
                onFailure = {
                }
            )
        }
    }
}
