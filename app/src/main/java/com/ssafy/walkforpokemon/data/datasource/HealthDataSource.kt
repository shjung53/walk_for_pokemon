package com.ssafy.walkforpokemon.data.datasource

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.fitness.HistoryClient
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import com.ssafy.walkforpokemon.SuccessOrFailure
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.suspendCancellableCoroutine
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TAG = "HealthDataSource"

class HealthDataSource @Inject constructor() {
    private lateinit var client: HistoryClient

    fun initializeClient(client: HistoryClient): Result<SuccessOrFailure> {
        return try {
            this.client = client
            Result.success(SuccessOrFailure.Success)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchStepCount(): Result<Int> {
        val now = LocalDateTime.now()
        val start = now.toLocalDate().atStartOfDay()
        val end = now.toLocalDate().atTime(LocalTime.MAX)
        val endSeconds = end.atZone(ZoneId.systemDefault()).toEpochSecond()
        val startSeconds = start.atZone(ZoneId.systemDefault()).toEpochSecond()

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.AGGREGATE_STEP_COUNT_DELTA)
            .setTimeRange(startSeconds, endSeconds, TimeUnit.SECONDS)
            .bucketByTime(1, TimeUnit.DAYS)
            .build()

        val result = suspendCancellableCoroutine { continuation ->
            client
                .readData(readRequest)
                .addOnSuccessListener { response ->
                    val stepCount =
                        (response.buckets[0].dataSets[0].dataPoints[0].getValue(Field.FIELD_STEPS)
                            .toString().toInt())
                    continuation.resume(Result.success(stepCount), null)
                    // Use response data here
                }
                .addOnFailureListener({ e ->
                    Log.d(TAG, "fetchStepCount() called with: e = $e")
                })
        }

        return result
    }
}
