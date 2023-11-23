package com.ssafy.walkforpokemon.dialogs

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.ssafy.walkforpokemon.R
import com.ssafy.walkforpokemon.databinding.DialogDrawConfirmBinding

private const val TAG = "FitnessRequiredDialog_싸피"

class FitnessRequiredDialog : DialogFragment() {

    private var _binding: DialogDrawConfirmBinding? = null
    private val binding get() = _binding!!
    private val fitnessAppName = "com.google.android.apps.fitness"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()  called with: savedInstanceState = $savedInstanceState")
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogDrawConfirmBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(
            TAG,
            "onViewCreated() called with: view = $view, savedInstanceState = $savedInstanceState",
        )

        this.isCancelable = false

        binding.message.text = getString(R.string.install_fitness)

        binding.cancelButton.setOnClickListener {
            requireActivity().finishAffinity()

            System.runFinalization() // 현재 작업중인 쓰레드가 다 종료되면, 종료 시키라는 명령어이다.

            System.exit(0) //
        }
        binding.confirmButton.setOnClickListener {
            val url = "market://details?id=" + fitnessAppName
            val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(i)
            this.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView() called")
        _binding = null
    }
}
