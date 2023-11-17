package com.ssafy.walkforpokemon

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ssafy.walkforpokemon.adapter.DictionaryAdapter
import com.ssafy.walkforpokemon.databinding.FragmentDictionaryBinding
import com.ssafy.walkforpokemon.dto.Pokemon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


private const val TAG = "DictionaryFragment_싸피"

class DictionaryFragment : Fragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private lateinit var _binding: FragmentDictionaryBinding

    private val binding get() = _binding!!


    private var pokeDataList = mutableListOf<Pokemon>()

    private lateinit var dictionaryAdapter: DictionaryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentDictionaryBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = Firebase.firestore

        CoroutineScope(Dispatchers.IO).launch {
            try {

                db.collection("pokemon")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            pokeDataList.add(
                                Pokemon(
                                    id = document.data.get("id").toString().toInt(),
                                    name = document.data.get("name") as String,
                                    nameKorean = document.data.get("nameKorean") as String,
                                    imageOfficial = document.data.get("imageOfficial") as String,
                                    image3D = document.data.get("image3D") as String,
                                    isLegendary = document.data.get("isLegendary") as Boolean,
                                    isMythical = document.data.get("isMythical") as Boolean,
                                    percentage = document.data.get("percentage") as Double,
                                    type = document.data.get("type") as List<String>
                                )
                            )
                            Log.d(
                                TAG,
                                "onViewCreated: ${document.id} => ${pokeDataList.size} , ${pokeDataList[pokeDataList.size - 1]}"
                            )

                        }
                    }
                    .addOnFailureListener {
                        Log.w(TAG, "onViewCreated: ", it)
                    }.await()

                withContext(Dispatchers.Main) {
                    pokeDataList.sortBy { it.id }
                    Log.d(TAG, "my first : ${pokeDataList[0]} , ${pokeDataList.size}")

                    dictionaryAdapter = DictionaryAdapter(requireContext(), pokeDataList)
                    dictionaryAdapter.notifyDataSetChanged()

                    binding.recyclerview.adapter = dictionaryAdapter
                    binding.recyclerview.layoutManager = GridLayoutManager(requireContext(), 3)

                }
            } catch (e: Exception) {
                Log.d(TAG, "onViewCreated: 통신 에러.... ${e}")
            }
        }


    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DictionaryFragment().apply {

            }
    }
}
