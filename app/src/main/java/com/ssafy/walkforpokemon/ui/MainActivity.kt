package com.ssafy.walkforpokemon.ui

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
import com.ssafy.walkforpokemon.R
import com.ssafy.walkforpokemon.databinding.ActivityMainBinding
import com.ssafy.walkforpokemon.dialogs.LoadingDialog
import com.ssafy.walkforpokemon.dialogs.LoadingView
import com.ssafy.walkforpokemon.util.CustomToast
import com.ssafy.walkforpokemon.viewmodels.DictionaryViewModel
import com.ssafy.walkforpokemon.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), LoadingView {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fitnessOptions: FitnessOptions
    private val mainViewModel: MainViewModel by viewModels()
    private val dictionaryViewModel: DictionaryViewModel by viewModels()
    private lateinit var loadingDialog: LoadingDialog

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkFitnessPermissions()
        loadingDialog = LoadingDialog(this)

        val userId = intent.getStringExtra("userId")

        if (userId == null) {
            CustomToast.createAndShow(this, "로그인에 문제가 발생했습니다")
            finishAffinity()
        } else {
            initUserAndPokemonList(userId)
        }

        mainViewModel.myPokemonSet.observe(this) {
            dictionaryViewModel.updateUserPokemonList(mainViewModel.mainPokemon.value ?: 0, it)
        }

        mainViewModel.mainPokemon.observe(this) {
            dictionaryViewModel.updateUserPokemonList(
                it,
                mainViewModel.myPokemonSet.value ?: mutableSetOf(),
            )
        }
    }

    private fun showToast(message: String) {
        CustomToast.createAndShow(this, message)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initUserAndPokemonList(userId: String) {
        CoroutineScope(Dispatchers.Main).launch {
            dictionaryViewModel.initPokemonList().fold(
                onSuccess = {},
                onFailure = {
                    showToast("포켓몬 정보 초기화에 실패했습니다.")
                    finishAffinity()
                },
            )

            mainViewModel.setUserId(userId).fold(
                onSuccess = {
                },
                onFailure = {
                    showToast("유저 정보 초기화에 실패했습니다.")
                    finishAffinity()
                },
            )
        }

        CoroutineScope(Dispatchers.Main).launch {
            mainViewModel.refreshStepCount(this@MainActivity).fold(
                onSuccess = { },
                onFailure = { showToast(getString(R.string.fail_refresh_step_count)) },
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

    override fun showLoading() {
        if (!loadingDialog.isShowing) {
            loadingDialog.show()
        }
    }

    override fun hideLoading() {
        if (loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
    }
}
