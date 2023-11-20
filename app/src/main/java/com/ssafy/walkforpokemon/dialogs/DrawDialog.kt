package com.ssafy.walkforpokemon.dialogs

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ssafy.walkforpokemon.R
import com.ssafy.walkforpokemon.databinding.DialogDrawConfirmBinding
import com.ssafy.walkforpokemon.viewmodels.DictionaryViewModel
import com.ssafy.walkforpokemon.viewmodels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

private const val TAG = "DrawDialog"

class DrawDialog : DialogFragment() {

    private var _binding: DialogDrawConfirmBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()
    private val dictionaryViewModel: DictionaryViewModel by activityViewModels()
    private var newPokemonId: Int = 0
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

        binding.root.setBackgroundColor(Color.TRANSPARENT)

        binding.message.text = "1000 마일리지를 소모하여 뽑기를 진행하시겠습니까?"

        binding.cancelButton.setOnClickListener {
            this.dismiss()
        }
        binding.confirmButton.setOnClickListener {
            newPokemonId = getNewPokemonId()
            mainViewModel.drawPokemon(newPokemonId)

            mainViewModel.user.observe(this) {
                lifecycleScope.launch {
                    withContext(Dispatchers.Default) {dictionaryViewModel.fetchUserPokemonList(it)}
                    for(pokemonId in it.myPokemons) {
                        if(pokemonId == newPokemonId) {
                            findNavController().navigate(R.id.drawFragment)
                            this@DrawDialog.dismiss()
                        }
                    }
                    Log.d(TAG, "onViewCreated() called: ${it.myPokemons}")
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
