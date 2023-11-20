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

    private var _userId: MutableLiveData<String> = MutableLiveData()
    val userId get() = _userId

    private var _user: MutableLiveData<User> = MutableLiveData(User(""))
    val user get() = _user

    fun fetchUserId() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                fetchUserIdUseCase.invoke().fold(
                    onSuccess = { _userId.value = it },
                    onFailure = {},
                )
            }
            _user.value = User(id = userId.value ?: "")
        }
    }

    fun fetchUser(id: String) {
        viewModelScope.launch {
            fetchUserUseCase.invoke(id).fold(
                onSuccess = { result ->
                    if (result.id.isEmpty()) {
                        registerUser(id)
                    } else {
                        _user.value = result
                    }
                },
                onFailure = { throwable ->
                    Log.d("fetchUser", throwable.message.toString())
                },
            )
        }
    }

    private fun registerUser(id: String) {
        viewModelScope.launch {
            registerUserUseCase.invoke(id).fold(
                onSuccess = {},
                onFailure = {
                },
            )
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
    fun fetchStepCount() {
        viewModelScope.launch {
            fetchStepCountUseCase.invoke().fold(
                onSuccess = {},
                onFailure = {
                },
            )
        }
    }
}
