package com.ssafy.walkforpokemon.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.walkforpokemon.LoginStatus
import com.ssafy.walkforpokemon.domain.login.LoginWithNaverUseCase
import com.ssafy.walkforpokemon.domain.login.RefreshLoginStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginWithNaverUseCase: LoginWithNaverUseCase,
    private val refreshLoginStatusUseCase: RefreshLoginStatusUseCase,
) : ViewModel() {

    private val _loginStatus = MutableLiveData<LoginStatus>()

    val loginStatus get() = _loginStatus

    init {
        _loginStatus.value = LoginStatus.Logout
    }

    fun onClickNaverLogin(context: Context) {
        viewModelScope.launch {
            loginWithNaverUseCase.invoke(context).fold(
                onSuccess = { refreshLoginStatus() },
                onFailure = {}
            )
        }
    }

    private fun refreshLoginStatus() {
        viewModelScope.launch {
            refreshLoginStatusUseCase.invoke().fold(
                onSuccess = { _loginStatus.value = it },
                onFailure = {
                }
            )
        }
    }
}
