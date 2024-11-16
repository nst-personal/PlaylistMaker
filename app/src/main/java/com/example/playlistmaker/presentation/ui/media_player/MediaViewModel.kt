package com.example.playlistmaker.presentation.ui.media_player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactors.media.MediaInteractor
import com.example.playlistmaker.domain.interactors.track.TrackHistoryInteractor
import com.example.playlistmaker.presentation.ui.media_player.interfaces.MediaScreenState
import com.example.playlistmaker.presentation.ui.media_player.interfaces.MediaState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MediaViewModel(
    trackHistoryInteractor: TrackHistoryInteractor,
    val mediaInteractor: MediaInteractor,
): ViewModel() {
    private var loadingTrackLiveData = MutableLiveData<MediaScreenState>()
    private var timerJob: Job? = null
    fun getLoadingTrackLiveData(): LiveData<MediaScreenState> = loadingTrackLiveData
    init {
        val data = trackHistoryInteractor.findLast()
        loadingTrackLiveData.postValue(MediaScreenState.Ready(data))
        mediaInteractor.init(
            data.previewUrl,
            {
                loadingTrackLiveData.postValue(MediaScreenState.Ready(data))
                loadingTrackLiveData.postValue(MediaScreenState.State(MediaState.STATE_PREPARED))
            },
            {
                loadingTrackLiveData.postValue(MediaScreenState.Completed(data))
                loadingTrackLiveData.postValue(MediaScreenState.State(MediaState.STATE_PREPARED))
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

    private fun stopTimer() {
        timerJob?.cancel()
    }

    companion object {
        private const val DELAY = 500L
    }
}