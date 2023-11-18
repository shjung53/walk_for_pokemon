package com.ssafy.walkforpokemon.domain.user

import com.ssafy.walkforpokemon.data.repository.UserRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class FetchUserIdUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke() = userRepository.fetchUserId()
}
