package com.ssafy.walkforpokemon.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.ssafy.walkforpokemon.GlobalApplication
import com.ssafy.walkforpokemon.R
import com.ssafy.walkforpokemon.databinding.DialogLoadingBinding

class LoadingDialog(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DialogLoadingBinding.inflate(layoutInflater)

        this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        Glide.with(context).load(R.raw.charmander).into(binding.loadingImage)

        setContentView(binding.root)
    }

}
