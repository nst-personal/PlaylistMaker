package com.example.playlistmaker.presentation.ui.media.fragments

import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactors.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.ui.media.fragments.interfaces.playlist.screen.PlaylistEditScreenState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class PlaylistEditViewModel (
    playlistInteractor: PlaylistInteractor
): PlaylistCreateViewModel(playlistInteractor) {

    fun showPlaylist(playlistId: Long) {
        viewModelScope.launch {
            playlistInteractor.getPlaylistById(playlistId)
                .onEach { data ->
                    loadingPlaylistLiveData.postValue(PlaylistEditScreenState.PlaylistContent(data))
                }
                .launchIn(this)
        }
    }

    fun updatePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.updatePlaylist(playlist)
            loadingPlaylistLiveData.postValue(
                PlaylistEditScreenState.PlaylistUpdateCompletedContent(
                    playlist
                ))
        }
    }

}