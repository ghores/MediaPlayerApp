package com.example.mediaplayerapp.presentation.common

import androidx.fragment.app.Fragment
import com.example.mediaplayerapp.MediaPlayerApp
import com.example.mediaplayerapp.di.AppContainer
import com.example.mediaplayerapp.di.AppViewModelFactory

fun Fragment.appContainer(): AppContainer {
    return (requireActivity().application as MediaPlayerApp).appContainer
}

fun Fragment.viewModelFactory(): AppViewModelFactory {
    return AppViewModelFactory(appContainer())
}
