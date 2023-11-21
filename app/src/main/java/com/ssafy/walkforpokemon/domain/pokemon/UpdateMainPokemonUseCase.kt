package com.ssafy.walkforpokemon.domain.pokemon

import com.ssafy.walkforpokemon.SuccessOrFailure
import com.ssafy.walkforpokemon.data.dataclass.User
import com.ssafy.walkforpokemon.data.repository.UserRepository
import javax.inject.Inject

class UpdateMainPokemonUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(user: User): Result<SuccessOrFailure> {
        userRepository.updateMainPokemon(user).fold(
            onSuccess = { return Result.success(it) },
            onFailure = { return Result.failure(it) },
        )
    }
}
