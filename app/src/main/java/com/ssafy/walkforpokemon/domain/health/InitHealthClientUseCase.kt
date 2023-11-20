package com.ssafy.walkforpokemon.domain.health

import com.google.android.gms.fitness.HistoryClient
import com.ssafy.walkforpokemon.SuccessOrFailure
import com.ssafy.walkforpokemon.data.repository.HealthRepository
import javax.inject.Inject

class InitHealthClientUseCase @Inject constructor(private val healthRepository: HealthRepository) {
    operator fun invoke(healthClient: HistoryClient): Result<SuccessOrFailure> {
        healthRepository.initializeClient(healthClient).fold(
            onSuccess = { return Result.success(it) },
            onFailure = { return Result.failure(it) },
        )
    }
}
