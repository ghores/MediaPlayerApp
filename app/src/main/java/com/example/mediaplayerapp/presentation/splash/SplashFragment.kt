package com.example.mediaplayerapp.presentation.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.mediaplayerapp.MainActivity
import com.example.mediaplayerapp.R
import com.example.mediaplayerapp.databinding.FragmentSplashBinding
import com.example.mediaplayerapp.presentation.common.viewModelFactory
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SplashViewModel by viewModels { viewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).setDrawBehindStatusBar(true)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigateToHome.collect {
                    findNavController().navigate(R.id.splashToViewPagerFragment)
                }
            }
        }
    }

    override fun onDestroyView() {
        (requireActivity() as MainActivity).setDrawBehindStatusBar(false)
        binding.splashAnimation.cancelAnimation()
        _binding = null
        super.onDestroyView()
    }
}
