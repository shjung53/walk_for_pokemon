package com.ssafy.walkforpokemon

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.ssafy.walkforpokemon.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fitnessOptions: FitnessOptions
    private val mainViewModel: MainViewModel by viewModels { MainViewModel.Factory }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigation()
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main) {
                createHealthClient()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createHealthClient() {
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
                fitnessOptions
            )
        } else {
            accessGoogleFit()
        }
    }

    private fun initNavigation() {
        // 네비게이션 호스트
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navigationHost) as NavHostFragment
        // 네비게이션 컨트롤러
        val navController = navHostFragment.navController
        // 바인딩
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)

        binding.bottomNavigation.itemIconTintList = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> when (requestCode) {
                8888 -> accessGoogleFit()
                else -> {
                    // Result wasn't from Google Fit
                }
            }

            else -> {
                // Permission not granted
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun accessGoogleFit() {
        val account = GoogleSignIn.getAccountForExtension(this, fitnessOptions)
        mainViewModel.initHealthClient(Fitness.getHistoryClient(this, account))
    }
}
