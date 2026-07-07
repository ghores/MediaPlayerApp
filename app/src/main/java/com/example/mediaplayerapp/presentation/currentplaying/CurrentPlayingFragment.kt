package com.example.mediaplayerapp.presentation.currentplaying

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.cleveroad.play_widget.PlayLayout
import com.example.mediaplayerapp.R
import com.example.mediaplayerapp.databinding.FragmentCurrentPlayingBinding
import com.example.mediaplayerapp.player.PlaybackUiState
import com.example.mediaplayerapp.player.RepeatMode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class CurrentPlayingFragment : Fragment() {

    private var _binding: FragmentCurrentPlayingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CurrentPlayingViewModel by activityViewModels()
    private var isUserSeeking = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrentPlayingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPlayLayout()
        binding.favoriteButton.setOnClickListener { viewModel.toggleFavorite() }
        observeViewModel()
        binding.playLayout.startRevealAnimation()
    }

    private fun setupPlayLayout() {
        binding.playLayout.setOnProgressChangedListener(object : PlayLayout.OnProgressChangedListener {
            override fun onPreSetProgress() {
                isUserSeeking = true
            }

            override fun onProgressChanged(progress: Float) {
                viewModel.seekTo(progress)
                isUserSeeking = false
            }
        })

        binding.playLayout.setOnButtonsClickListener(object : PlayLayout.OnButtonsClickListener {
            override fun onShuffleClicked() = viewModel.toggleShuffle()
            override fun onSkipPreviousClicked() = viewModel.playPrevious()
            override fun onSkipNextClicked() = viewModel.playNext()
            override fun onRepeatClicked() = viewModel.cycleRepeatMode()
            override fun onPlayButtonClicked() = viewModel.togglePlayPause()
        })
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        renderState(state)
                    }
                }

                launch {
                    viewModel.isCurrentFavorite.collect { isFavorite ->
                        renderFavorite(isFavorite)
                    }
                }
            }
        }
    }

    private fun renderFavorite(isFavorite: Boolean) {
        val icon = if (isFavorite) {
            R.drawable.ic_favorite
        } else {
            R.drawable.ic_favorite_border
        }
        binding.favoriteButton.setImageResource(icon)
    }

    private fun renderState(state: PlaybackUiState) {
        val music = state.currentMusic ?: return

        binding.songTitle.text = music.title
        binding.songArtist.text = music.singerName
        binding.songAlbum.text = music.albumName
        binding.playLayout.setImageURI(music.coverArtUri)

        if (!isUserSeeking) {
            binding.playLayout.setProgress(state.progressFraction)
        }

        binding.currentTime.text = formatDuration(state.currentPositionMs)
        binding.totalTime.text = formatDuration(state.durationMs)

        val icon = if (state.isPlaying) {
            android.R.drawable.ic_media_pause
        } else {
            android.R.drawable.ic_media_play
        }
        binding.playLayout.playButton.setImageResource(icon)

        renderShuffleAndRepeat(state)
    }

    private fun renderShuffleAndRepeat(state: PlaybackUiState) {
        val whiteColor = ContextCompat.getColor(requireContext(), R.color.white)
        val activeColor = ContextCompat.getColor(requireContext(), R.color.b1)

        val shuffleColor = if (state.isShuffleEnabled) activeColor else whiteColor
        binding.playLayout.ivShuffle.apply {
            setImageResource(R.drawable.ic_shuffle)
            setColorFilter(shuffleColor)
        }

        val repeatIcon = if (state.repeatMode == RepeatMode.REPEAT_ONE) {
            R.drawable.ic_repeat_one
        } else {
            R.drawable.ic_repeat
        }
        val repeatColor = if (state.repeatMode == RepeatMode.OFF) whiteColor else activeColor
        binding.playLayout.ivRepeat.apply {
            setImageResource(repeatIcon)
            setColorFilter(repeatColor)
        }
    }

    private fun formatDuration(durationMs: Long): String {
        val totalSeconds = (durationMs / 1000).coerceAtLeast(0)
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
