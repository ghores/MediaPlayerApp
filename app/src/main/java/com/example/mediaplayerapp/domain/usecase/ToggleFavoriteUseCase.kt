package com.example.mediaplayerapp.domain.usecase

import com.example.mediaplayerapp.data.repository.FavoriteRepository
import com.example.mediaplayerapp.domain.model.Music

class ToggleFavoriteUseCase(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(music: Music) {
        favoriteRepository.toggleFavorite(music)
    }
}
