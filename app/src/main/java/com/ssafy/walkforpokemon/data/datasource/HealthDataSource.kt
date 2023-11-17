package com.ssafy.walkforpokemon.data.datasource

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.fitness.HistoryClient
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class HealthDataSource() {
    private lateinit var client: HistoryClient

    fun initializeClient(client: HistoryClient) {
        this.client = client
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchStepCount() {
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

        client
            .readData(readRequest)
            .addOnSuccessListener { response ->
                response.buckets[0].dataSets[0].dataPoints[0].getValue(Field.FIELD_STEPS)
                // Use response data here
                Log.d("fetchStepCount", "OnSuccess()")
            }
            .addOnFailureListener({ e -> Log.d("fetchStepCount", "OnFailure()", e) })
    }
}
