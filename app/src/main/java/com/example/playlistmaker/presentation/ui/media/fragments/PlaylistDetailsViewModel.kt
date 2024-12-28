package com.example.playlistmaker.presentation.ui.media.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactors.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.interactors.track.TrackHistoryInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.media.fragments.interfaces.playlist.screen.PlaylistDetailsScreenState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class PlaylistDetailsViewModel (
    private val playlistInteractor: PlaylistInteractor,
    private val trackHistoryInteractor: TrackHistoryInteractor,
): ViewModel() {

    private var loadingPlaylistLiveData = MutableLiveData<PlaylistDetailsScreenState>()
    fun getLoadingPlaylistLiveData(): LiveData<PlaylistDetailsScreenState> = loadingPlaylistLiveData

    fun showPlaylist(playlistId: Long) {
        viewModelScope.launch {
            playlistInteractor.getPlaylistById(playlistId)
                .onEach { data ->
                    loadingPlaylistLiveData.postValue(PlaylistDetailsScreenState.PlaylistContent(data))
                }
                .launchIn(this)
        }
    }

    fun saveTrack(track: Track) {
        trackHistoryInteractor.updateLast(track)
    }

    fun deleteTrack(playlist: Playlist, track: Track) {
        viewModelScope.launch {
            playlistInteractor.deletePlaylistTrack(track, playlist)
            showPlaylist(playlist.playlistId)
        }
    }

}