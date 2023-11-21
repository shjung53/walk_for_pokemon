package com.ssafy.walkforpokemon.data.repository

import android.content.Context
import com.navercorp.nid.oauth.NidOAuthLoginState
import com.ssafy.walkforpokemon.LoginStatus
import com.ssafy.walkforpokemon.SuccessOrFailure
import com.ssafy.walkforpokemon.data.dataclass.User
import com.ssafy.walkforpokemon.data.datasource.UserDataSource
import java.lang.Exception
import javax.inject.Inject

class UserRepository @Inject constructor(private val userDataSource: UserDataSource) {

    suspend fun loginWithNaver(context: Context): Result<SuccessOrFailure> {
        userDataSource.authenticateWithNaver(context).fold(
            onSuccess = { return Result.success(it) },
            onFailure = { return Result.failure(it) },
        )
    }

    suspend fun loginWithGoogle(context: Context): Result<SuccessOrFailure> {
        userDataSource.authenticateWith
    }

    fun getLoginStatus(): Result<LoginStatus> {
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
            onFailure = { return Result.failure(it) },
        )
    }

    suspend fun fetchUser(id: String): Result<User> =
        userDataSource.fetchUser(id)

    suspend fun registerUser(id: String): Result<SuccessOrFailure> {
        userDataSource.registerUser(id).fold(
            onSuccess = { return Result.success(it) },
            onFailure = { return Result.failure(it) },
        )
    }

    suspend fun addNewPokemon(userId: String, pokemonId: Int): Result<SuccessOrFailure> {
        userDataSource.addPokemonToUser(userId, pokemonId).fold(
            onSuccess = { return Result.success(it) },
            onFailure = { return Result.failure(it) },
        )
    }

    suspend fun updateMainPokemon(user: User): Result<SuccessOrFailure> {
        userDataSource.updateMainPokemon(user).fold(
            onSuccess = { return Result.success(it) },
            onFailure = { return Result.failure(it) },
        )
    }
}
