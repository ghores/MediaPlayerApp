package com.example.mediaplayerapp.domain.usecase

import com.example.mediaplayerapp.data.repository.MusicRepository
import com.example.mediaplayerapp.domain.model.MusicFolder
import com.example.mediaplayerapp.domain.model.folderPath
import javax.inject.Inject

class GetMusicFoldersUseCase @Inject constructor(
    private val musicRepository: MusicRepository
) {
    suspend operator fun invoke(): List<MusicFolder> {
        return musicRepository.getAllMusic()
            .filter { it.folderPath.isNotEmpty() }
            .groupBy { it.folderPath }
            .map { (path, songs) ->
                MusicFolder(
                    name = path.substringAfterLast('/').ifEmpty { path },
                    path = path,
                    songs = songs
                )
            }
            .sortedBy { it.name.lowercase() }
    }
}
