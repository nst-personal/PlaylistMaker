package com.example.playlistmaker.presentation.ui.media_player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactors.favorites.FavoriteTrackInteractor
import com.example.playlistmaker.domain.interactors.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.interactors.track.TrackHistoryInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.media.fragments.interfaces.playlist.screen.PlaylistListScreenState
import com.example.playlistmaker.presentation.ui.media_player.interfaces.MediaScreenState
import com.example.playlistmaker.presentation.ui.media_player.interfaces.MediaState
import com.example.playlistmaker.presentation.ui.media_player.interfaces.TrackState
import com.example.playlistmaker.services.MediaPlayerControl
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MediaViewModel(
    private val trackHistoryInteractor: TrackHistoryInteractor,
    private val favoriteTrackInteractor: FavoriteTrackInteractor,
    private val playlistInteractor: PlaylistInteractor
): ViewModel() {
    private var loadingTrackLiveData = MutableLiveData<MediaScreenState>()
    private var trackLiveData = MutableLiveData<TrackState>()
    private var loadingPlaylistLiveData = MutableLiveData<PlaylistListScreenState>()
    private var playlistStateLiveData = MutableLiveData<Boolean>()
    fun getLoadingTrackLiveData(): LiveData<MediaScreenState> = loadingTrackLiveData
    fun getTrackLiveData(): LiveData<TrackState> = trackLiveData
    fun getLoadingPlaylistLiveData(): LiveData<PlaylistListScreenState> = loadingPlaylistLiveData
    fun getPlaylistStateLiveData(): LiveData<Boolean> = playlistStateLiveData
    private val playerState = MutableLiveData<MediaState>(MediaState.Default())
    fun observePlayerState(): LiveData<MediaState> = playerState

    init {
        requestLoadingTrackLiveData()
        requestTrackLiveData()
    }

    private var audioPlayerControl: MediaPlayerControl? = null

    fun setAudioPlayerControl(audioPlayerControl: MediaPlayerControl) {
        this.audioPlayerControl = audioPlayerControl
        viewModelScope.launch {
            audioPlayerControl.getMediaState().collect {
                playerState.postValue(it)
            }
        }
    }

    fun requestTrackLiveData() {
        val data = this.trackHistoryInteractor.findLast()
        trackLiveData.postValue(TrackState.Favorite(data, data.isFavorite))
    }

    fun requestLoadingTrackLiveData() {
        val data = this.trackHistoryInteractor.findLast()
        loadingTrackLiveData.postValue(MediaScreenState.Ready(data))
    }

    fun addTrack(track: Track) {
        viewModelScope.launch {
            favoriteTrackInteractor.addTrack(track);
            trackLiveData.postValue(TrackState.Favorite(track, true))
        }
    }

    fun updatePlaylist(track: Track, playlist: Playlist) {
        viewModelScope.launch {
            val isUpdated = playlistInteractor.updatePlaylist(track, playlist)
            if (isUpdated) {
                loadingPlaylistLiveData.postValue(PlaylistListScreenState.PlaylistListUpdatedContent(track, playlist))
            } else {
                loadingPlaylistLiveData.postValue(PlaylistListScreenState.PlaylistListNotUpdatedContent(track, playlist))
            }
        }
    }

    fun removeTrack(track: Track) {
        viewModelScope.launch {
            favoriteTrackInteractor.removeTrack(track);
            trackLiveData.postValue(TrackState.Favorite(track, false))
        }
    }

    fun showPlaylist() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists()
                .onEach { data ->
                    loadingPlaylistLiveData.postValue(PlaylistListScreenState.PlaylistListContent(data))
                }
                .launchIn(this)
        }
    }

    fun openPlaylist() {
        playlistStateLiveData.postValue(true)
    }

    fun closePlaylist() {
        playlistStateLiveData.postValue(false)
    }

}