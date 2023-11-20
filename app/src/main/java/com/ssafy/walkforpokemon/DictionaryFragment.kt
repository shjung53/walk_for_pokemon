package com.ssafy.walkforpokemon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ssafy.walkforpokemon.adapter.DictionaryAdapter
import com.ssafy.walkforpokemon.databinding.FragmentDictionaryBinding
import com.ssafy.walkforpokemon.viewmodels.DictionaryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DictionaryFragment : Fragment() {
    private var _binding: FragmentDictionaryBinding? = null
    private val binding get() = _binding!!
    private val dictionaryViewModel: DictionaryViewModel by activityViewModels()
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

        dictionaryAdapter = DictionaryAdapter(
            requireContext(),
            dictionaryViewModel.pokemonList.value ?: emptyList(),
        )
        binding.recyclerview.adapter = dictionaryAdapter

        dictionaryViewModel.pokemonList.observe(requireActivity()) {
            dictionaryAdapter.itemList = it
            dictionaryAdapter.notifyDataSetChanged()
        }
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
