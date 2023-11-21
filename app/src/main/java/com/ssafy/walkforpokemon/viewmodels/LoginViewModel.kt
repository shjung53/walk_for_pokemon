package com.ssafy.walkforpokemon.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.walkforpokemon.LoginStatus
import com.ssafy.walkforpokemon.domain.login.LoginWithGoogleUseCase
import com.ssafy.walkforpokemon.domain.login.LoginWithNaverUseCase
import com.ssafy.walkforpokemon.domain.login.RefreshLoginStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val refreshLoginStatusUseCase: RefreshLoginStatusUseCase,
) : ViewModel() {

    private val _loginStatus = MutableLiveData<LoginStatus>(LoginStatus.Logout)

    val loginStatus get() = _loginStatus

//    fun onClickNaverLogin(context: Context) {
//        viewModelScope.launch {
//            loginWithNaverUseCase.invoke(context).fold(
//                onSuccess = { refreshLoginStatus() },
//                onFailure = {},
//            )
//        }
//    }

    fun onClickGoogleLogin(context: Context) {
        viewModelScope.launch {
            loginWithGoogleUseCase.invoke(context).fold(
                onSuccess = {refreshLoginStatus()},
                onFailure = {},
            )
        }
    }

    private fun refreshLoginStatus() {
        viewModelScope.launch {
            refreshLoginStatusUseCase.invoke().fold(
                onSuccess = { _loginStatus.value = it },
                onFailure = {
                },
            )
        }
    }
}
