package com.ssafy.walkforpokemon.domain.user

import com.ssafy.walkforpokemon.data.dataclass.User
import com.ssafy.walkforpokemon.data.repository.UserRepository
import javax.inject.Inject

class FetchUserUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: String): Result<User> =
        userRepository.fetchUser(userId)
}
