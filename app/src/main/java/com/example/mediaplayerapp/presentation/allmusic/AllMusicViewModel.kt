package com.example.mediaplayerapp.presentation.allmusic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaplayerapp.domain.usecase.GetAllMusicUseCase
import com.example.mediaplayerapp.domain.usecase.PlayMusicUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AllMusicViewModel(
    private val getAllMusicUseCase: GetAllMusicUseCase,
    private val playMusicUseCase: PlayMusicUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AllMusicUiState())
    val uiState: StateFlow<AllMusicUiState> = _uiState.asStateFlow()

    private val _navigateToPlayer = Channel<Unit>(Channel.BUFFERED)
    val navigateToPlayer = _navigateToPlayer.receiveAsFlow()

    fun onPermissionGranted() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            runCatching { getAllMusicUseCase() }
                .onSuccess { songs ->
                    _uiState.update { it.copy(songs = songs, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "خطا در بارگذاری آهنگ‌ها"
                        )
                    }
                }
        }
    }

    fun onSongSelected(index: Int) {
        val songs = _uiState.value.songs
        if (index !in songs.indices) return

        playMusicUseCase(songs, index)
        viewModelScope.launch {
            _navigateToPlayer.send(Unit)
        }
    }
}
