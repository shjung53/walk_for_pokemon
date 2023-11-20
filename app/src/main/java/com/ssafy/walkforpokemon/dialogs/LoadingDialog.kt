package com.ssafy.walkforpokemon.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.ssafy.walkforpokemon.R
import com.ssafy.walkforpokemon.databinding.DialogLoadingBinding

class LoadingDialog : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = DialogLoadingBinding.inflate(layoutInflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        Glide.with(this).load(R.raw.charmander).into(binding.loadingImage)

        return binding.root
    }
}
