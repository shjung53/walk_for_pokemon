package com.ssafy.walkforpokemon.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.fitness.HistoryClient
import com.ssafy.walkforpokemon.SuccessOrFailure
import com.ssafy.walkforpokemon.data.datasource.HealthDataSource
import javax.inject.Inject

class HealthRepository @Inject constructor(private val healthDataSource: HealthDataSource) {

    fun initializeClient(healthClient: HistoryClient): Result<SuccessOrFailure> {
        healthDataSource.initializeClient(healthClient).fold(
            onSuccess = { return Result.success(it) },
            onFailure = { return Result.failure(it) },
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchStepCount(): Result<Int> {
        healthDataSource.fetchStepCount().fold(
            onSuccess = { return Result.success(it) },
            onFailure = { return Result.failure(it) },
        )
    }
}
