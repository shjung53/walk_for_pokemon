package com.ssafy.walkforpokemon.domain.health

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.ssafy.walkforpokemon.data.repository.HealthRepository
import javax.inject.Inject

class FetchStepCountUseCase @Inject constructor(private val healthRepository: HealthRepository) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(context: Context): Result<Int> {
        healthRepository.fetchStepCount(context).fold(
            onSuccess = { return Result.success(it) },
            onFailure = { return Result.failure(it) },
        )
    }
}
