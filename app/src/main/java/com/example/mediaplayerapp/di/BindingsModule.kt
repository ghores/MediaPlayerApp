package com.example.mediaplayerapp.di

import com.example.mediaplayerapp.data.datasource.FavoriteDataSource
import com.example.mediaplayerapp.data.datasource.LocalFavoriteDataSource
import com.example.mediaplayerapp.data.datasource.LocalMusicDataSource
import com.example.mediaplayerapp.data.datasource.MusicDataSource
import com.example.mediaplayerapp.data.repository.FavoriteRepository
import com.example.mediaplayerapp.data.repository.FavoriteRepositoryImpl
import com.example.mediaplayerapp.data.repository.MusicRepository
import com.example.mediaplayerapp.data.repository.MusicRepositoryImpl
import com.example.mediaplayerapp.player.ExoPlaybackController
import com.example.mediaplayerapp.player.PlaybackController
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BindingsModule {

    @Binds
    abstract fun bindMusicDataSource(impl: LocalMusicDataSource): MusicDataSource

    @Binds
    abstract fun bindFavoriteDataSource(impl: LocalFavoriteDataSource): FavoriteDataSource

    @Binds
    abstract fun bindMusicRepository(impl: MusicRepositoryImpl): MusicRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(impl: FavoriteRepositoryImpl): FavoriteRepository

    @Binds
    @Singleton
    abstract fun bindPlaybackController(impl: ExoPlaybackController): PlaybackController
}
