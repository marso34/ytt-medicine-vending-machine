package com.wonchihyeon.ytt_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.wonchihyeon.ytt_android.R
import com.wonchihyeon.ytt_android.databinding.FragmentGptBinding

// GPT 페이지
class GptFragment : Fragment() {

    private lateinit var binding: FragmentGptBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gpt, container, false)

        binding.tipTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_GptFragment_to_tipFragment)
        }

        binding.homeTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_GptFragment_to_homeFragment)
        }

        binding.talkTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_GptFragment_to_talkFragment)
        }

        binding.storeTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_GptFragment_to_storeFragment)
        }

        return binding.root
    }
}