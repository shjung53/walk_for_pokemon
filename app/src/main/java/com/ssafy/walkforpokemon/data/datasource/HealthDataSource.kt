package com.ssafy.walkforpokemon.data.datasource

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import kotlinx.coroutines.suspendCancellableCoroutine
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TAG = "HealthDataSource"

class HealthDataSource @Inject constructor() {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchStepCount(context: Context): Result<Int> {
        val fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .build()
        val account = GoogleSignIn.getAccountForExtension(context, fitnessOptions)

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
            Fitness.getHistoryClient(context, account)
                .readData(readRequest)
                .addOnSuccessListener { response ->
                    var stepCount = 0
                    Log.d(TAG, "fetchStepCount() called with: response = ${response.buckets}")
                    if (response.buckets[0].dataSets[0].dataPoints.size > 0) {
                        stepCount = response.buckets[0].dataSets[0].dataPoints[0].getValue(
                            Field.FIELD_STEPS,
                        ).toString().toInt()
                    }
                    continuation.resume(Result.success(stepCount), null)
                    // Use response data here
                }
                .addOnFailureListener { e ->
                    continuation.resume(Result.failure(e), null)
                    Log.d(TAG, "fetchStepCount() called with: e = $e")
                }
        }

        return result
    }
}
