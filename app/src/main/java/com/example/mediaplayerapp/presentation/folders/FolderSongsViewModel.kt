package com.example.mediaplayerapp.presentation.folders

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaplayerapp.domain.model.Music
import com.example.mediaplayerapp.domain.model.folderPath
import com.example.mediaplayerapp.domain.usecase.GetAllMusicUseCase
import com.example.mediaplayerapp.domain.usecase.PlayMusicUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderSongsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getAllMusicUseCase: GetAllMusicUseCase,
    private val playMusicUseCase: PlayMusicUseCase
) : ViewModel() {

    private val targetFolderPath: String =
        savedStateHandle[FoldersFragment.ARG_FOLDER_PATH] ?: ""

    val folderName: String =
        savedStateHandle[FoldersFragment.ARG_FOLDER_NAME] ?: ""

    private val _songs = MutableStateFlow<List<Music>>(emptyList())
    val songs: StateFlow<List<Music>> = _songs.asStateFlow()

    private val _navigateToPlayer = Channel<Unit>(Channel.BUFFERED)
    val navigateToPlayer = _navigateToPlayer.receiveAsFlow()

    init {
        loadSongs()
    }

    private fun loadSongs() {
        viewModelScope.launch {
            runCatching { getAllMusicUseCase() }
                .onSuccess { allSongs ->
                    _songs.value = allSongs.filter { it.folderPath == targetFolderPath }
                }
        }
    }

    fun onSongSelected(index: Int) {
        val currentSongs = _songs.value
        if (index !in currentSongs.indices) return

        playMusicUseCase(currentSongs, index)
        viewModelScope.launch {
            _navigateToPlayer.send(Unit)
        }
    }
}
