package com.ssafy.walkforpokemon.dialogs

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ssafy.walkforpokemon.R
import com.ssafy.walkforpokemon.databinding.DialogDrawConfirmBinding
import com.ssafy.walkforpokemon.util.CustomToast
import com.ssafy.walkforpokemon.viewmodels.DictionaryViewModel
import com.ssafy.walkforpokemon.viewmodels.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DrawDialog : DialogFragment(), LoadingView {

    private var _binding: DialogDrawConfirmBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()
    private val dictionaryViewModel: DictionaryViewModel by activityViewModels()
    private var duplication = false
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

        loadingDialog = LoadingDialog(requireActivity())
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

        initView()

        initClickListener()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initClickListener() {
        binding.cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.confirmButton.setOnClickListener {
            showLoading()
            checkMileage()
            val newPokemonId = dictionaryViewModel.getNewPokemonId()
            checkDuplication(newPokemonId)

            CoroutineScope(Dispatchers.Main).launch {
                mainViewModel.drawPokemon(newPokemonId, duplication).fold(
                    onSuccess = {
                        dictionaryViewModel.updateUserPokemonList(
                            mainViewModel.mainPokemon.value ?: -1,
                            mainViewModel.myPokemonSet.value ?: mutableSetOf(),
                        )
                        val action = DrawDialogDirections.actionDrawDialogToDrawFragment(
                            newPokemonId,
                            duplication,
                        )
                        hideLoading()
                        if (mainViewModel.myPokemonSet.value?.contains(newPokemonId) == true) {
                            findNavController().navigate(action)
                        } else {
                            mainViewModel.myPokemonSet.value?.add(newPokemonId)
                            findNavController().navigateUp()
                        }
                    },
                    onFailure = {
                        hideLoading()
                        CustomToast.createAndShow(requireActivity(), "문제가 발생했습니다. 잠시후 다시 시도해주세요")
                        findNavController().navigateUp()
                    },
                )
            }
        }
    }

    private fun checkDuplication(newPokemonId: Int) {
        val currentMyPokemonSet = mainViewModel.myPokemonSet.value ?: mutableSetOf()
        if (currentMyPokemonSet.contains(newPokemonId)) {
            duplication = true
        }
    }

    private fun checkMileage() {
        if ((mainViewModel.currentMileage.value ?: 0) < 1000) {
            CustomToast.createAndShow(requireActivity(), "마일리지가 부족합니다")
            findNavController().navigateUp()
        }
    }

    private fun initView() {
        this.isCancelable = false
        binding.message.text = getString(R.string.draw_message)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
