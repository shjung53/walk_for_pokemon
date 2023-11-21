package com.ssafy.walkforpokemon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.ssafy.walkforpokemon.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.drawBtn.setOnClickListener {
            findNavController().navigate(R.id.drawDialog, null)
        }

        /* TODO
            1. user data에서 마일리지, 걸음 수 받아와서 xml에 집어넣기
         */
        binding.nowMileage.text = String.format("%,d", 10) // 여기 집어넣기
        binding.nowSteps.text = String.format("%,d", 10000000)

        val navController = binding.root.findNavController()

        val dictionaryTransitionOption = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(R.anim.slide_in_up)
            .setExitAnim(R.anim.stay)
            .setPopExitAnim(R.anim.slide_out_down)
            .setExitAnim(R.anim.stay)
            .setPopUpTo(navController.graph.startDestinationId, false)
            .build()

        binding.dictionaryBtn.setOnClickListener {
            navController.navigate(R.id.dictioniary, null, dictionaryTransitionOption)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}
