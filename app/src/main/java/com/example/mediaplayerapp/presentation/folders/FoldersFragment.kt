package com.example.mediaplayerapp.presentation.folders

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaplayerapp.R
import com.example.mediaplayerapp.adapter.FolderAdapter
import com.example.mediaplayerapp.databinding.FragmentFoldersBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FoldersFragment : Fragment() {

    private var _binding: FragmentFoldersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FoldersViewModel by viewModels()
    private var folderAdapter: FolderAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoldersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        requestAudioPermission()
    }

    private fun setupRecyclerView() {
        folderAdapter = FolderAdapter(emptyList()) { folder ->
            findNavController().navigate(
                R.id.folderSongsFragment,
                bundleOf(ARG_FOLDER_PATH to folder.path, ARG_FOLDER_NAME to folder.name)
            )
        }
        binding.recyclerViewFolders.apply {
            adapter = folderAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            )
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    folderAdapter?.submitList(state.folders)
                    binding.emptyStateText.visibility =
                        if (!state.isLoading && state.folders.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun requestAudioPermission() {
        Dexter.withContext(requireContext())
            .withPermission(requiredAudioPermission())
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    viewModel.onPermissionGranted()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) = Unit

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }).check()
    }

    private fun requiredAudioPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        folderAdapter = null
        _binding = null
    }

    companion object {
        const val ARG_FOLDER_PATH = "folderPath"
        const val ARG_FOLDER_NAME = "folderName"
    }
}
