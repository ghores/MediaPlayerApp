package com.example.mediaplayerapp.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mediaplayerapp.presentation.allmusic.AllMusicViewModel
import com.example.mediaplayerapp.presentation.currentplaying.CurrentPlayingViewModel
import com.example.mediaplayerapp.presentation.favorite.FavoriteMusicViewModel
import com.example.mediaplayerapp.presentation.splash.SplashViewModel

class AppViewModelFactory(
    private val appContainer: AppContainer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AllMusicViewModel::class.java) -> {
                AllMusicViewModel(
                    getAllMusicUseCase = appContainer.getAllMusicUseCase,
                    playMusicUseCase = appContainer.playMusicUseCase
                ) as T
            }

            modelClass.isAssignableFrom(CurrentPlayingViewModel::class.java) -> {
                CurrentPlayingViewModel(
                    playbackController = appContainer.playbackController,
                    toggleFavoriteUseCase = appContainer.toggleFavoriteUseCase,
                    observeFavoritesUseCase = appContainer.observeFavoritesUseCase
                ) as T
            }

            modelClass.isAssignableFrom(FavoriteMusicViewModel::class.java) -> {
                FavoriteMusicViewModel(
                    observeFavoritesUseCase = appContainer.observeFavoritesUseCase,
                    playMusicUseCase = appContainer.playMusicUseCase
                ) as T
            }

            modelClass.isAssignableFrom(SplashViewModel::class.java) -> {
                SplashViewModel() as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
