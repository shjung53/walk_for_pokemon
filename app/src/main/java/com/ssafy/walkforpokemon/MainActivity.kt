package com.ssafy.walkforpokemon

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.ssafy.walkforpokemon.databinding.ActivityMainBinding
import com.ssafy.walkforpokemon.viewmodels.DictionaryViewModel
import com.ssafy.walkforpokemon.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fitnessOptions: FitnessOptions
    private val mainViewModel: MainViewModel by viewModels()
    private val dictionaryViewModel: DictionaryViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkFitnessPermissions()

        val userId = intent.getStringExtra("userId")

        if (userId == null) {
            CustomToast.createAndShow(this, "로그인에 문제가 발생했습니다")
            finishAffinity()
        } else {
            mainViewModel.setUserId(this, userId)
        }
        dictionaryViewModel.initPokemonList()

        mainViewModel.myPokemonSet.observe(this) {
            dictionaryViewModel.updateUserPokemonList(mainViewModel.mainPokemon.value ?: 0, it)
        }
        mainViewModel.mainPokemon.observe(this) {
            dictionaryViewModel.updateUserPokemonList(
                mainViewModel.mainPokemon.value ?: 0,
                mainViewModel.myPokemonSet.value ?: mutableSetOf(),
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkFitnessPermissions() {
        fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .build()

        val account = GoogleSignIn.getAccountForExtension(this, fitnessOptions)

        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                this, // your activity
                8888, // e.g. 1
                account,
                fitnessOptions,
            )
        } else {
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> when (requestCode) {
                8888 -> {}
                else -> {
                    // Result wasn't from Google Fit
                }
            }

            else -> {
                // Permission not granted
            }
        }
    }
}
