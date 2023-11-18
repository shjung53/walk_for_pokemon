package com.ssafy.walkforpokemon.domain.login

import com.ssafy.walkforpokemon.LoginStatus
import com.ssafy.walkforpokemon.data.repository.UserRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class RefreshLoginStatusUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(): Result<LoginStatus> {
        userRepository.getLoginStatus().fold(
            onSuccess = { return Result.success(it) },
            onFailure = { return Result.failure(it) }
        )
    }
}
