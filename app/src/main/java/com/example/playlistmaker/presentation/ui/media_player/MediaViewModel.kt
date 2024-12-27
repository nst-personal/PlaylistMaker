package com.example.playlistmaker.presentation.ui.media_player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.interactors.favorites.FavoriteTrackInteractor
import com.example.playlistmaker.domain.interactors.media.MediaInteractor
import com.example.playlistmaker.domain.interactors.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.interactors.track.TrackHistoryInteractor
import com.example.playlistmaker.presentation.ui.media.fragments.interfaces.playlist.screen.PlaylistListScreenState
import com.example.playlistmaker.presentation.ui.media_player.interfaces.MediaScreenState
import com.example.playlistmaker.presentation.ui.media_player.interfaces.MediaState
import com.example.playlistmaker.presentation.ui.media_player.interfaces.TrackState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MediaViewModel(
    val trackHistoryInteractor: TrackHistoryInteractor,
    val favoriteTrackInteractor: FavoriteTrackInteractor,
    val mediaInteractor: MediaInteractor,
    val playlistInteractor: PlaylistInteractor
): ViewModel() {
    private var loadingTrackLiveData = MutableLiveData<MediaScreenState>()
    private var trackLiveData = MutableLiveData<TrackState>()
    private var loadingPlaylistLiveData = MutableLiveData<PlaylistListScreenState>()
    private var playlistStateLiveData = MutableLiveData<Boolean>()
    private var timerJob: Job? = null
    fun getLoadingTrackLiveData(): LiveData<MediaScreenState> = loadingTrackLiveData
    fun getTrackLiveData(): LiveData<TrackState> = trackLiveData
    fun getLoadingPlaylistLiveData(): LiveData<PlaylistListScreenState> = loadingPlaylistLiveData
    fun getPlaylistStateLiveData(): LiveData<Boolean> = playlistStateLiveData

    init {
        requestLoadingTrackLiveData()
        requestTrackLiveData()
    }

    fun requestTrackLiveData() {
        val data = this.trackHistoryInteractor.findLast()
        trackLiveData.postValue(TrackState.Favorite(data, data.isFavorite))
    }

    fun requestLoadingTrackLiveData() {
        val data = this.trackHistoryInteractor.findLast()
        loadingTrackLiveData.postValue(MediaScreenState.Ready(data))
        mediaInteractor.init(
            data.previewUrl,
            {
                loadingTrackLiveData.postValue(MediaScreenState.Ready(data))
                loadingTrackLiveData.postValue(MediaScreenState.State(MediaState.STATE_PREPARED))
            },
            {
                loadingTrackLiveData.postValue(MediaScreenState.State(MediaState.STATE_PREPARED))
                loadingTrackLiveData.postValue(MediaScreenState.Completed(data))
                stopTimer()
            }
        )
    }

    fun release() {
        mediaInteractor.release()
    }

    fun stop() {
        mediaInteractor.stop()
    }

    fun start() {
        mediaInteractor.start()
        loadingTrackLiveData.postValue(
            MediaScreenState.State(MediaState.STATE_PLAYING)
        )
        startTimer()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (mediaInteractor.isPlaying()) {
                delay(DELAY)
                loadingTrackLiveData.postValue(MediaScreenState.Time(mediaInteractor.currentPosition()))
            }
        }
    }

    fun pause() {
        mediaInteractor.pause()
        loadingTrackLiveData.postValue(MediaScreenState.State(MediaState.STATE_PAUSED))
        stopTimer()
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

    private fun stopTimer() {
        timerJob?.cancel()
    }

    companion object {
        private const val DELAY = 300L
    }
}