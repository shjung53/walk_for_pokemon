package com.ssafy.walkforpokemon.domain.login

import android.content.Context
import com.ssafy.walkforpokemon.SuccessOrFailure
import com.ssafy.walkforpokemon.data.repository.UserRepository
import javax.inject.Inject

class LoginWithNaverUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(context: Context): Result<SuccessOrFailure> =
        userRepository.loginWithNaver(context)
}
