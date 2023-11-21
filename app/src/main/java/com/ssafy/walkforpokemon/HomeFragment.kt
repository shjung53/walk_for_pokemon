package com.ssafy.walkforpokemon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.ssafy.walkforpokemon.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var _binding: FragmentHomeBinding
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.drawBtn.setOnClickListener {
            /* TODO
                뽑기 dialog를 띄워봅시다.
             */
        }

        /* TODO
            1. user data에서 마일리지, 걸음 수 받아와서 xml에 집어넣기
         */
        binding.nowMileage.text = String.format("%,d", 10) // 여기 집어넣기
        binding.nowSteps.text = String.format("%,d", 10000000)

        val navController = binding.root.findNavController()

        val dictionaryTransitionOption = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)
            .setPopUpTo(navController.graph.startDestinationId, false)
            .build()

        binding.dictionaryBtn.setOnClickListener {
            navController.navigate(R.id.dictioniary, null, dictionaryTransitionOption)
        }

    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
            }
    }
}
