package com.example.mediaplayerapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mediaplayerapp.R
import com.example.mediaplayerapp.databinding.FragmentCurrentPlayingBinding
import com.example.mediaplayerapp.databinding.FragmentSplashBinding

class CurrentPlayingFragment : Fragment() {
    private var _binding: FragmentCurrentPlayingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCurrentPlayingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}