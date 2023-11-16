package com.ssafy.walkforpokemon

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.ssafy.walkforpokemon.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val oauthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            }

            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Toast.makeText(
                    this@LoginActivity,
                    "errorCode:$errorCode, errorDesc:$errorDescription",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        }


        binding.naverLoginButton.setOnClickListener {
            NaverIdLoginSDK.authenticate(this, oauthLoginCallback)
        }
    }
}
