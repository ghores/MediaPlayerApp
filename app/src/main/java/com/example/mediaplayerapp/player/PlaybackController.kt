package com.example.mediaplayerapp.player

import com.example.mediaplayerapp.domain.model.Music
import kotlinx.coroutines.flow.StateFlow

interface PlaybackController {
    val playbackState: StateFlow<PlaybackUiState>

    fun play(playlist: List<Music>, startIndex: Int)
    fun togglePlayPause()
    fun playNext()
    fun playPrevious()
    fun seekTo(progressFraction: Float)
    fun toggleShuffle()
    fun cycleRepeatMode()
    fun release()
}
