package com.example.mediaplayerapp.domain.usecase

import com.example.mediaplayerapp.data.repository.FavoriteRepository
import com.example.mediaplayerapp.domain.model.Music
import kotlinx.coroutines.flow.StateFlow

class ObserveFavoritesUseCase(
    private val favoriteRepository: FavoriteRepository
) {
    operator fun invoke(): StateFlow<List<Music>> {
        return favoriteRepository.favorites
    }
}
