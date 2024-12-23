package com.example.playlistmaker.presentation.ui.media.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactors.playlist.PlaylistInteractor
import com.example.playlistmaker.presentation.ui.media.fragments.interfaces.playlist.screen.PlaylistListScreenState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class PlaylistListViewModel (
    private val playlistInteractor: PlaylistInteractor,
): ViewModel() {

    private var loadingPlaylistLiveData = MutableLiveData<PlaylistListScreenState>()
    fun getLoadingPlaylistLiveData(): LiveData<PlaylistListScreenState> = loadingPlaylistLiveData

    fun showPlaylist() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists()
                .onEach { data ->
                    loadingPlaylistLiveData.postValue(PlaylistListScreenState.PlaylistListContent(data))
                }
                .launchIn(this)
        }
    }


}