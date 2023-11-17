package com.ssafy.walkforpokemon.data.repository

import com.ssafy.walkforpokemon.SuccessOrFailure
import com.ssafy.walkforpokemon.data.dataclass.User
import com.ssafy.walkforpokemon.data.datasource.UserDataSource

class UserRepository(private val userDataSource: UserDataSource) {

    suspend fun retrieveUserId() =
        userDataSource.retrieveUserId()

    suspend fun fetchUser(id: String): Result<User> {
        userDataSource.fetchUser(id).fold(
            onSuccess = { return Result.success(it) },
            onFailure = { return Result.failure(it) }
        )
    }

    suspend fun registerUser(id: String): Result<SuccessOrFailure> {
        userDataSource.registerUser(id).fold(
            onSuccess = { return Result.success(it) },
            onFailure = { return Result.failure(it) }
        )
    }

}
