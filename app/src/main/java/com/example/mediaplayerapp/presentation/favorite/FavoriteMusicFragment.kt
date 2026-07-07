package com.example.mediaplayerapp.presentation.favorite

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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaplayerapp.R
import com.example.mediaplayerapp.adapter.MusicAdapter
import com.example.mediaplayerapp.databinding.FragmentFavoriteMusicBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteMusicFragment : Fragment() {

    private var _binding: FragmentFavoriteMusicBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoriteMusicViewModel by viewModels()
    private var favoriteAdapter: MusicAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        favoriteAdapter = MusicAdapter(emptyList(), requireContext()) { index ->
            viewModel.onFavoriteSelected(index)
        }
        binding.recyclerViewFavorite.apply {
            adapter = favoriteAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            )
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.favorites.collect { songs ->
                        favoriteAdapter?.submitList(songs)
                        binding.emptyStateText.visibility =
                            if (songs.isEmpty()) View.VISIBLE else View.GONE
                    }
                }

                launch {
                    viewModel.navigateToPlayer.collect {
                        findNavController().navigate(R.id.currentPlayingFragment)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        favoriteAdapter = null
        _binding = null
    }
}
