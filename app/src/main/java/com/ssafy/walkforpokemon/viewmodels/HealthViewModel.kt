package com.ssafy.walkforpokemon.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.gms.fitness.HistoryClient
import com.ssafy.walkforpokemon.data.datasource.HealthDataSource
import com.ssafy.walkforpokemon.data.repository.HealthRepository

class HealthViewModel(private val healthRepository: HealthRepository) : ViewModel() {

    fun initHealthClient(healthClient: HistoryClient) {
        healthRepository.initializeClient(healthClient)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchStepCount() {
        healthRepository.fetchStepCount()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HealthViewModel(
                    healthRepository = HealthRepository(
                        healthDataSource = HealthDataSource(
                        ),
                    ),
                )
            }
        }
    }
}
