package com.example.mediaplayerapp.presentation.currentplaying

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaplayerapp.domain.usecase.ObserveFavoritesUseCase
import com.example.mediaplayerapp.domain.usecase.ToggleFavoriteUseCase
import com.example.mediaplayerapp.player.PlaybackController
import com.example.mediaplayerapp.player.PlaybackUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrentPlayingViewModel @Inject constructor(
    private val playbackController: PlaybackController,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    observeFavoritesUseCase: ObserveFavoritesUseCase
) : ViewModel() {

    val uiState: StateFlow<PlaybackUiState> = playbackController.playbackState

    val isCurrentFavorite: StateFlow<Boolean> =
        combine(playbackController.playbackState, observeFavoritesUseCase()) { playback, favorites ->
            val currentPath = playback.currentMusic?.filePath ?: return@combine false
            favorites.any { it.filePath == currentPath }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    fun togglePlayPause() {
        playbackController.togglePlayPause()
    }

    fun playNext() {
        playbackController.playNext()
    }

    fun playPrevious() {
        playbackController.playPrevious()
    }

    fun seekTo(progressFraction: Float) {
        playbackController.seekTo(progressFraction)
    }

    fun toggleShuffle() {
        playbackController.toggleShuffle()
    }

    fun cycleRepeatMode() {
        playbackController.cycleRepeatMode()
    }

    fun toggleFavorite() {
        val music = uiState.value.currentMusic ?: return
        viewModelScope.launch {
            toggleFavoriteUseCase(music)
        }
    }
}
