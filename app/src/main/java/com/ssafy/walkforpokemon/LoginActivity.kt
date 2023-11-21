package com.ssafy.walkforpokemon

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ssafy.walkforpokemon.databinding.ActivityLoginBinding
import com.ssafy.walkforpokemon.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.naverLoginButton.setOnClickListener {
            loginViewModel.onClickNaverLogin(this)
        }

        loginViewModel.loginStatus.observe(this) {
            if (it == LoginStatus.Login) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
