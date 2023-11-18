package com.ssafy.walkforpokemon.domain.health

import android.os.Build
import androidx.annotation.RequiresApi
import com.ssafy.walkforpokemon.SuccessOrFailure
import com.ssafy.walkforpokemon.data.repository.HealthRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class FetchStepCountUseCase @Inject constructor(private val healthRepository: HealthRepository) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(): Result<Int> {
        healthRepository.fetchStepCount().fold(
            onSuccess = { return Result.success(it) },
            onFailure = { return Result.failure(it) }
        )
    }
}
