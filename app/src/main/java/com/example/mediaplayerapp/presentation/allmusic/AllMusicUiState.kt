package com.example.mediaplayerapp.presentation.allmusic

import com.example.mediaplayerapp.domain.model.Music

data class AllMusicUiState(
    val songs: List<Music> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
