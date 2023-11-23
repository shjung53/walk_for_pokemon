package com.ssafy.walkforpokemon.dialogs

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ssafy.walkforpokemon.CustomToast
import com.ssafy.walkforpokemon.R
import com.ssafy.walkforpokemon.databinding.DialogDrawConfirmBinding
import com.ssafy.walkforpokemon.viewmodels.DictionaryViewModel
import com.ssafy.walkforpokemon.viewmodels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class DrawDialog : DialogFragment() {

    private var _binding: DialogDrawConfirmBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()
    private val dictionaryViewModel: DictionaryViewModel by activityViewModels()
    private var duplication = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        this.isCancelable = false

        binding.message.text = "1000 마일리지를 소모하여 뽑기를 진행하시겠습니까?"

        binding.cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.confirmButton.setOnClickListener {
            if ((mainViewModel.currentMileage.value ?: 0) < 1000) {
                CustomToast.createAndShow(requireActivity(), "마일리지가 부족합니다").show()
                this@DrawDialog.dismiss()
                return@setOnClickListener
            }
            val newPokemonId = dictionaryViewModel.getNewPokemonId()
            if (newPokemonId < 1) {
                CustomToast.createAndShow(requireActivity(), "문제가 발생했습니다. 다시 시도해주세요!").show()
                return@setOnClickListener
            }
            val nowSet = mainViewModel.myPokemonSet.value ?: mutableSetOf()
            if (nowSet.contains(newPokemonId)) {
                duplication = true
            }
            mainViewModel.drawPokemon(newPokemonId, duplication)

            mainViewModel.myPokemonSet.observe(this) {
                lifecycleScope.launch {
                    withContext(Dispatchers.Main) {
                        dictionaryViewModel.updateUserPokemonList(
                            mainViewModel.mainPokemon.value ?: 0,
                            mainViewModel.myPokemonSet.value ?: mutableSetOf(),
                        )
                    }

                    if (it.contains(newPokemonId)) {
                        val action = DrawDialogDirections.actionDrawDialogToDrawFragment(
                            newPokemonId,
                            duplication,
                        )
                        findNavController().navigate(action)
                        this@DrawDialog.dismiss()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
