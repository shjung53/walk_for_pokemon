package com.ssafy.walkforpokemon.domain.health

import android.os.Build
import androidx.annotation.RequiresApi
import com.ssafy.walkforpokemon.SuccessOrFailure
import com.ssafy.walkforpokemon.data.repository.UserRepository
import javax.inject.Inject

class AddMileageUseCase @Inject constructor(private val userRepository: UserRepository) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(userId: String, newCurrentMileage: Int): Result<SuccessOrFailure> {
        userRepository.addMileage(userId, newCurrentMileage).fold(
            onSuccess = { return Result.success(it) },
            onFailure = { return Result.failure(it) },
        )
    }
}
