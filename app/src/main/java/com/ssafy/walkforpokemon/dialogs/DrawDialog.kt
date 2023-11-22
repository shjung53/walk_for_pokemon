package com.ssafy.walkforpokemon.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ssafy.walkforpokemon.R
import com.ssafy.walkforpokemon.data.dataclass.User
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.isCancelable = false

        binding.message.text = "1000 마일리지를 소모하여 뽑기를 진행하시겠습니까?"

        binding.cancelButton.setOnClickListener {
            findNavController().navigate(R.id.home, null)
        }
        binding.confirmButton.setOnClickListener {
            val newPokemonId = getNewPokemonId()
            val nowSet = mainViewModel.myPokemonSet.value ?: mutableSetOf()
            if (nowSet.contains(newPokemonId)) duplication = true
            mainViewModel.drawPokemon(newPokemonId)

            mainViewModel.myPokemonSet.observe(this) {
                lifecycleScope.launch {
                    withContext(Dispatchers.Main) {
                        dictionaryViewModel.updateUserPokemonList(
                            mainViewModel.user.value ?: User(""),
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

    private fun getNewPokemonId(): Int {
        /* TODO
            1. firebase data에서 percentage 조정해서 저장해놓기
            2. 도감 데이터 받아와서 로컬에 저장해놓은 거에서 퍼센티지 받아오기
         */
        return Random.nextInt(1, 152)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
