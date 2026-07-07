package com.example.mediaplayerapp.data.repository

import com.example.mediaplayerapp.data.datasource.FavoriteDataSource
import com.example.mediaplayerapp.di.ApplicationScope
import com.example.mediaplayerapp.domain.model.Music
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteDataSource: FavoriteDataSource,
    @ApplicationScope scope: CoroutineScope
) : FavoriteRepository {

    private val _favorites = MutableStateFlow<List<Music>>(emptyList())
    override val favorites: StateFlow<List<Music>> = _favorites.asStateFlow()

    init {
        scope.launch {
            _favorites.value = favoriteDataSource.getFavorites()
        }
    }

    override suspend fun toggleFavorite(music: Music) {
        val current = _favorites.value
        val updated = if (current.any { it.filePath == music.filePath }) {
            current.filterNot { it.filePath == music.filePath }
        } else {
            current + music
        }

        _favorites.value = updated
        favoriteDataSource.saveFavorites(updated)
    }

    override fun isFavorite(filePath: String): Boolean {
        return _favorites.value.any { it.filePath == filePath }
    }
}
