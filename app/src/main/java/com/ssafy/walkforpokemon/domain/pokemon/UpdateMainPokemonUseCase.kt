package com.ssafy.walkforpokemon.domain.pokemon

import com.ssafy.walkforpokemon.SuccessOrFailure
import com.ssafy.walkforpokemon.data.repository.UserRepository
import javax.inject.Inject

class UpdateMainPokemonUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(userId: String, pokemonId: Int): Result<SuccessOrFailure> {
        userRepository.updateMainPokemon(userId, pokemonId).fold(
            onSuccess = { return Result.success(it) },
            onFailure = { return Result.failure(it) },
        )
    }
}
