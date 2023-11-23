package com.ssafy.walkforpokemon.data.repository

import com.ssafy.walkforpokemon.SuccessOrFailure
import com.ssafy.walkforpokemon.data.dataclass.User
import com.ssafy.walkforpokemon.data.datasource.UserDataSource
import javax.inject.Inject

class UserRepository @Inject constructor(private val userDataSource: UserDataSource) {

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

    suspend fun updateMainPokemon(userId: String, pokemonId: Int): Result<SuccessOrFailure> {
        userDataSource.updateMainPokemon(userId, pokemonId).fold(
            onSuccess = { return Result.success(it) },
            onFailure = { return Result.failure(it) },
        )
    }

    suspend fun useMileageUseCase(
        userId: String,
        newCurrentMileage: Int,
    ): Result<SuccessOrFailure> {
        userDataSource.updateCurrentMileageUseCase(userId, newCurrentMileage).fold(
            onSuccess = { return Result.success(it) },
            onFailure = { return Result.failure(it) },
        )
    }

    suspend fun addMileageUseCase(
        userId: String,
        newCurrentMileage: Int,
        newAddedMileage: Int,
    ): Result<SuccessOrFailure> {
        userDataSource.updateCurrentAndAddedMileageUseCase(
            userId,
            newCurrentMileage,
            newAddedMileage,
        ).fold(
            onSuccess = { return Result.success(it) },
            onFailure = { return Result.failure(it) },
        )
    }
}
