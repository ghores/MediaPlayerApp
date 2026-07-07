package com.example.mediaplayerapp.presentation.folders

import com.example.mediaplayerapp.domain.model.MusicFolder

data class FoldersUiState(
    val folders: List<MusicFolder> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
