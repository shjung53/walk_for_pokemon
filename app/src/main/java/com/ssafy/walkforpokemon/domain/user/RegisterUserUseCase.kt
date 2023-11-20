package com.ssafy.walkforpokemon.domain.user

import com.ssafy.walkforpokemon.SuccessOrFailure
import com.ssafy.walkforpokemon.data.repository.UserRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: String): Result<SuccessOrFailure> =
        userRepository.registerUser(userId)
}
