package com.ssafy.walkforpokemon.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ssafy.walkforpokemon.data.dataclass.User
import com.ssafy.walkforpokemon.data.datasource.UserDataSource
import com.ssafy.walkforpokemon.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private var _user: MutableLiveData<User> = MutableLiveData()
    private val user get() = _user

    init {
        _user.value = User("")
    }

    private suspend fun retrieveUserId() {
        val id: String
        withContext(Dispatchers.IO) {
            id = userRepository.retrieveUserId()
        }
        val temp = _user.value?.copy()
        _user.value = temp?.copy(id = id)

    }

    fun fetchUser() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                retrieveUserId()
                user.value?.let {
                    userRepository.fetchUser(it.id).fold(
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
                userRepository.registerUser(it.id).fold(
                    onSuccess = {},
                    onFailure = {
                        Log.d("registerUser", "실패")
                    }
                )
            }
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                UserViewModel(
                    userRepository = UserRepository(
                        userDataSource = UserDataSource(
                        ),
                    ),
                )
            }
        }
    }
}
