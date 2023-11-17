package com.ssafy.walkforpokemon.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.fitness.HistoryClient
import com.ssafy.walkforpokemon.data.datasource.HealthDataSource

class HealthRepository(private val healthDataSource: HealthDataSource) {

    fun initializeClient(healthClient: HistoryClient) {
        healthDataSource.initializeClient(healthClient)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchStepCount(){
        healthDataSource.fetchStepCount()
    }
}
