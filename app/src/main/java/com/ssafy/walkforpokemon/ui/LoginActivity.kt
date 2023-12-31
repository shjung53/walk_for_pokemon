package com.ssafy.walkforpokemon.ui

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.ssafy.walkforpokemon.databinding.ActivityLoginBinding
import com.ssafy.walkforpokemon.dialogs.LoadingDialog
import com.ssafy.walkforpokemon.dialogs.LoadingView
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "LoginActivity"

@AndroidEntryPoint
class LoginActivity() : AppCompatActivity(), LoadingView {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loadingDialog: LoadingDialog

    // Google Sign In API와 호출할 구글 로그인 클라이언트
    private val googleSignInClient: GoogleSignInClient by lazy { getGoogleClient() }

    private val googleAuthLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)

            val tokenId = account.id
            try { // 로그인 잘 된 경우!!
                hideLoading()
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("userId", tokenId)
                }
                startActivity(
                    intent,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle(),
                )
                finish()
            } catch (e: ApiException) {
                Log.d(TAG, e.stackTraceToString())
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingDialog = LoadingDialog(this)

        binding.googleLoginBtn.setOnClickListener {
            showLoading()
            requestGoogleLogin()
        }

        val gsa = GoogleSignIn.getLastSignedInAccount(this)

        if (gsa != null && gsa.id != null) { // 이미 로그인된 사용자의 경우
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("userId", gsa.id)
            }
            startActivity(
                intent,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle(),
            )
            finish()
        }
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
