package com.ssafy.walkforpokemon.domain.user

import com.ssafy.walkforpokemon.SuccessOrFailure
import com.ssafy.walkforpokemon.data.repository.UserRepository
import javax.inject.Inject

class DrawPokemonUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: String, pokemonList: List<Int>): Result<SuccessOrFailure> {
        userRepository.addNewPokemon(userId, pokemonList).fold(
            onSuccess = { return Result.success(it) },
            onFailure = { return Result.failure(it) },
        )
    }
}
