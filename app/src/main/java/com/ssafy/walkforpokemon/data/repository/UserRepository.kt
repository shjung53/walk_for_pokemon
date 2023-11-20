package com.ssafy.walkforpokemon.data.repository

import android.content.Context
import com.navercorp.nid.oauth.NidOAuthLoginState
import com.ssafy.walkforpokemon.LoginStatus
import com.ssafy.walkforpokemon.SuccessOrFailure
import com.ssafy.walkforpokemon.data.dataclass.User
import com.ssafy.walkforpokemon.data.datasource.UserDataSource
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import javax.inject.Inject

class UserRepository @Inject constructor(private val userDataSource: UserDataSource) {

    suspend fun loginWithNaver(context: Context): Result<SuccessOrFailure> {
        userDataSource.authenticateWithNaver(context).fold(
            onSuccess = { return Result.success(it) },
            onFailure = { return Result.failure(it) }
        )
    }

    suspend fun getLoginStatus(): Result<LoginStatus> {
        return try {
            when (userDataSource.getLoginStatus()) {
                NidOAuthLoginState.OK -> Result.success(LoginStatus.Login)
                else -> Result.success(LoginStatus.Logout)
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }

    }

    suspend fun fetchUserId(): Result<String> {
        userDataSource.fetchUserId().fold(
            onSuccess = { return Result.success(it) },
            onFailure = { return Result.failure(it) }
        )
    }

    suspend fun fetchUser(id: String): Result<User> =
        userDataSource.fetchUser(id)


    suspend fun registerUser(id: String): Result<SuccessOrFailure> {
        userDataSource.registerUser(id).fold(
            onSuccess = { return Result.success(it) },
            onFailure = { return Result.failure(it) }
        )
    }

}