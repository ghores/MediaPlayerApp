package com.example.mediaplayerapp.player

import com.example.mediaplayerapp.domain.model.Music

data class PlaybackUiState(
    val currentMusic: Music? = null,
    val isPlaying: Boolean = false,
    val progressFraction: Float = 0f,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val isShuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF
)
