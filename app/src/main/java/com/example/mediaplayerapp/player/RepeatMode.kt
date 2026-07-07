package com.example.mediaplayerapp.player

enum class RepeatMode {
    OFF,
    REPEAT_ALL,
    REPEAT_ONE;

    fun next(): RepeatMode = when (this) {
        OFF -> REPEAT_ALL
        REPEAT_ALL -> REPEAT_ONE
        REPEAT_ONE -> OFF
    }
}
