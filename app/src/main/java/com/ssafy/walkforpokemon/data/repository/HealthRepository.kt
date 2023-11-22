package com.ssafy.walkforpokemon.data.repository

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.fitness.HistoryClient
import com.ssafy.walkforpokemon.SuccessOrFailure
import com.ssafy.walkforpokemon.data.datasource.HealthDataSource
import javax.inject.Inject

class HealthRepository @Inject constructor(private val healthDataSource: HealthDataSource) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchStepCount(context: Context): Result<Int> {
        healthDataSource.fetchStepCount(context).fold(
            onSuccess = { return Result.success(it) },
            onFailure = { return Result.failure(it) },
        )
    }
}
