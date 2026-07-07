package com.example.mediaplayerapp.presentation.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaplayerapp.domain.model.Music
import com.example.mediaplayerapp.domain.usecase.ObserveFavoritesUseCase
import com.example.mediaplayerapp.domain.usecase.PlayMusicUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class FavoriteMusicViewModel(
    observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val playMusicUseCase: PlayMusicUseCase
) : ViewModel() {

    val favorites: StateFlow<List<Music>> = observeFavoritesUseCase()

    private val _navigateToPlayer = Channel<Unit>(Channel.BUFFERED)
    val navigateToPlayer = _navigateToPlayer.receiveAsFlow()

    fun onFavoriteSelected(index: Int) {
        val songs = favorites.value
        if (index !in songs.indices) return

        playMusicUseCase(songs, index)
        viewModelScope.launch {
            _navigateToPlayer.send(Unit)
        }
    }
}
