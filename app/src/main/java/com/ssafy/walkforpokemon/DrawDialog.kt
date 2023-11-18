package com.ssafy.walkforpokemon

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ssafy.walkforpokemon.databinding.DialogDrawBinding

private const val TAG = "DrawDialog_싸피"

class DrawDialog : DialogFragment() {

    private lateinit var _binding: DialogDrawBinding
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogDrawBinding.inflate(layoutInflater)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var dialog: CustomDialog? = null

        dialog = CustomDialog(requireContext(), binding)
        dialog!!.build("1000 마일리지를 소모하여 뽑기를 진행하시겠습니까?", requireContext())

        return binding.root
    }


}
