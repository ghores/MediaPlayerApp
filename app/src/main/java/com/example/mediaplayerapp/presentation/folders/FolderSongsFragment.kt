package com.example.mediaplayerapp.presentation.folders

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
import com.example.mediaplayerapp.databinding.FragmentFolderSongsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FolderSongsFragment : Fragment() {

    private var _binding: FragmentFolderSongsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FolderSongsViewModel by viewModels()
    private var musicAdapter: MusicAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFolderSongsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.folderTitle.text = viewModel.folderName
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        musicAdapter = MusicAdapter(emptyList(), requireContext()) { index ->
            viewModel.onSongSelected(index)
        }
        binding.recyclerViewFolderSongs.apply {
            adapter = musicAdapter
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
                    viewModel.songs.collect { songs ->
                        musicAdapter?.submitList(songs)
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
        musicAdapter = null
        _binding = null
    }
}
