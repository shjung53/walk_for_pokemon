package com.ssafy.walkforpokemon

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ssafy.walkforpokemon.adapter.DictionaryAdapter
import com.ssafy.walkforpokemon.data.dataclass.Pokemon
import com.ssafy.walkforpokemon.databinding.FragmentDictionaryBinding
import com.ssafy.walkforpokemon.viewmodels.DictionaryViewModel
import com.ssafy.walkforpokemon.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DictionaryFragment : Fragment() {
    private var _binding: FragmentDictionaryBinding? = null
    private val binding get() = _binding!!
    private val dictionaryViewModel: DictionaryViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var dictionaryAdapter: DictionaryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDictionaryBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerViewAdapter()

        setAchievementText()

        dictionaryViewModel.myPokemonList.observe(requireActivity()) {
            dictionaryAdapter.submitList(it)
        }

        binding.closeButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setAchievementText() {
        val builder = SpannableStringBuilder()
        val text1 = "완성도 "
        val now = "${mainViewModel.myPokemonSet.value?.size ?: 0}"
        val total = " / ${dictionaryViewModel.pokemonList.value?.size ?: 151}"

        val startIndex = text1.length
        val endIndex = startIndex + now.length

        builder.append(text1).append(now).append(total)

        builder.setSpan(
            ForegroundColorSpan(requireActivity().getColor(R.color.type_fire)),
            startIndex, // start
            endIndex, // end
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE,
        )

        binding.achievement.text = builder
    }

    private fun setRecyclerViewAdapter() {
        dictionaryAdapter = DictionaryAdapter(
            requireContext(),
            dictionaryViewModel.myPokemonList.value ?: emptyList(),
        )
        binding.recyclerview.adapter = dictionaryAdapter

        dictionaryAdapter.setOnItemClickListener(object : DictionaryAdapter.OnItemClickListener {
            override fun onClick(pokemon: Pokemon) {
                if (!pokemon.isActive) {
                    Toast.makeText(requireActivity(), "아직 얻지 못한 포켓몬입니다!", Toast.LENGTH_SHORT).show()
                } else {
                    val action =
                        DictionaryFragmentDirections.actionDictionaryToDictionaryDetail(pokemon.id)
                    findNavController().navigate(action)
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            DictionaryFragment()
    }
}
