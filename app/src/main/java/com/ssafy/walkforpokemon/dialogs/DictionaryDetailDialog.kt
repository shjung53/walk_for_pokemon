package com.ssafy.walkforpokemon.dialogs

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.ssafy.walkforpokemon.R
import com.ssafy.walkforpokemon.data.dataclass.Pokemon
import com.ssafy.walkforpokemon.databinding.DialogDictionaryDetailBinding
import com.ssafy.walkforpokemon.ui.DrawFragmentArgs
import com.ssafy.walkforpokemon.util.CustomToast
import com.ssafy.walkforpokemon.viewmodels.DictionaryViewModel
import com.ssafy.walkforpokemon.viewmodels.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DictionaryDetailDialog : DialogFragment(), LoadingView {

    private var _binding: DialogDictionaryDetailBinding? = null
    private val binding: DialogDictionaryDetailBinding get() = _binding!!
    private val dictionaryViewModel: DictionaryViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

        loadingDialog = LoadingDialog(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogDictionaryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: DrawFragmentArgs by navArgs()
        val pokemonId: Int = args.pokemonId
        val pokemon = dictionaryViewModel.myPokemonList.value?.find { it.id == pokemonId }

        pokemon?.let {
            Glide.with(this).load(it.imageOfficial).into(binding.pokemonImage)
            binding.number.text = "no.${pokemon.id}"
            binding.pokemonName.text = it.nameKorean
            binding.type1.text = it.type[0]

            setTypeColor(it)

            if (it.isMain) {
                binding.medal.visibility = View.VISIBLE
                binding.setMainPokemonButton.visibility = View.GONE
            } else {
                binding.medal.visibility = View.GONE
                binding.setMainPokemonButton.visibility = View.VISIBLE
            }
        }

        binding.card.setOnClickListener {
            // 카드 클릭해서 다이얼로그 끄기 막기
        }

        binding.root.setOnClickListener {
            this.dismiss()
        }

        binding.setMainPokemonButton.setOnClickListener { button ->
            button.isClickable = false
            showLoading()
            CoroutineScope(Dispatchers.Main).launch {
                mainViewModel.updateMainPokemon(pokemonId).fold(
                    onSuccess = {
                        hideLoading()
                        findNavController().navigateUp()
                    },
                    onFailure = {
                        hideLoading()
                        CustomToast.createAndShow(
                            requireActivity(),
                            "대표 포켓몬 등록에 실패했습니다. 다시 시도해주세요!",
                        )
                        button.isClickable = true
                    },
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setTypeColor(it: Pokemon) {
        val type1Color = requireActivity().resources.getIdentifier(
            "type_${it.type[0]}",
            "color",
            requireActivity().packageName,
        )
        binding.type1.setBackgroundResource(type1Color)
        binding.card.strokeColor = requireActivity().getColor(type1Color)

        val hsv = FloatArray(3)
        Color.colorToHSV(requireActivity().getColor(type1Color), hsv)
        val saturationFactor = 0.1f
        hsv[1] *= saturationFactor
        val brightnessFactor = 1.5f
        hsv[2] *= brightnessFactor

        val newColor = Color.HSVToColor(hsv)
        binding.card.setCardBackgroundColor(newColor)

        if (it.type.size > 1) {
            binding.type2.text = it.type[1]
            binding.type2.visibility = View.VISIBLE
            val type2Color = requireActivity().resources.getIdentifier(
                "type_${it.type[1]}",
                "color",
                requireActivity().packageName,
            )
            binding.type2.setBackgroundResource(type2Color)
        }
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
