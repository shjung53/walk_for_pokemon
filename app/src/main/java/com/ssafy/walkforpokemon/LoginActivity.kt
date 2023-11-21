package com.ssafy.walkforpokemon

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.ssafy.walkforpokemon.databinding.ActivityLoginBinding
import com.ssafy.walkforpokemon.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    // Google Sign In API와 호출할 구글 로그인 클라이언트
    private val googleSignInClient: GoogleSignInClient by lazy { getGoogleClient() }


    private val googleAuthLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

            try { // 로그인 잘 된 경우!!
                val account = task.getResult(ApiException::class.java)

                // 이름, 이메일 등이 필요하다면
                val userName = account.givenName
                val serverAuth = account.serverAuthCode
                val tokenId = account.id

                Log.d(TAG, "my name: $userName, serverAuth: $serverAuth, tokenId: $tokenId")

                this.run {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(
                        intent,
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
                    )
                    finish()
                }


            } catch (e: ApiException) {
                Log.d(TAG, e.stackTraceToString())
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.googleLoginBtn.setOnClickListener {
            requestGoogleLogin()
        }

        var gsa = GoogleSignIn.getLastSignedInAccount(this)

        if (gsa != null && gsa.id != null) { // 이미 로그인된 사용자의 경우

        }

        loginViewModel.loginStatus.observe(this) {

        }


//        loginViewModel.loginStatus.observe(this) {
//            if (it == LoginStatus.Login) {
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
//                finish()
//            }
//        }
    }

    private fun requestGoogleLogin() { // 로그인 하는 함수
//        googleSignInClient.signOut()
        val signInIntent = googleSignInClient.signInIntent
        googleAuthLauncher.launch(signInIntent)
    }

    private fun getGoogleClient(): GoogleSignInClient {
        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestServerAuthCode(getString(R.string.google_login_client_id)) // string 파일에 저장해둔 client id 를 이용해 server authcode를 요청한다.
            .requestEmail() // 이메일도 요청할 수 있다.
            .build()

        return GoogleSignIn.getClient(this, googleSignInOption)
    }

}
