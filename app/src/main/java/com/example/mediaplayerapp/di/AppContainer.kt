package com.example.mediaplayerapp.di

import android.content.Context
import com.example.mediaplayerapp.data.datasource.FavoriteDataSource
import com.example.mediaplayerapp.data.datasource.LocalFavoriteDataSource
import com.example.mediaplayerapp.data.datasource.LocalMusicDataSource
import com.example.mediaplayerapp.data.datasource.MusicDataSource
import com.example.mediaplayerapp.data.repository.FavoriteRepository
import com.example.mediaplayerapp.data.repository.FavoriteRepositoryImpl
import com.example.mediaplayerapp.data.repository.MusicRepository
import com.example.mediaplayerapp.data.repository.MusicRepositoryImpl
import com.example.mediaplayerapp.domain.usecase.GetAllMusicUseCase
import com.example.mediaplayerapp.domain.usecase.ObserveFavoritesUseCase
import com.example.mediaplayerapp.domain.usecase.PlayMusicUseCase
import com.example.mediaplayerapp.domain.usecase.ToggleFavoriteUseCase
import com.example.mediaplayerapp.player.ExoPlaybackController
import com.example.mediaplayerapp.player.PlaybackController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class AppContainer(context: Context) {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val musicDataSource: MusicDataSource = LocalMusicDataSource(context)
    private val musicRepository: MusicRepository = MusicRepositoryImpl(musicDataSource)

    private val favoriteDataSource: FavoriteDataSource = LocalFavoriteDataSource(context)
    private val favoriteRepository: FavoriteRepository =
        FavoriteRepositoryImpl(favoriteDataSource, applicationScope)

    val playbackController: PlaybackController = ExoPlaybackController(context)

    val getAllMusicUseCase = GetAllMusicUseCase(musicRepository)
    val playMusicUseCase = PlayMusicUseCase(playbackController)
    val toggleFavoriteUseCase = ToggleFavoriteUseCase(favoriteRepository)
    val observeFavoritesUseCase = ObserveFavoritesUseCase(favoriteRepository)

    fun release() {
        playbackController.release()
        applicationScope.cancel()
    }
}
