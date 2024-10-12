package com.example.playlistmaker.presentation.ui.media_player

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.interactors.media.MediaInteractor
import com.example.playlistmaker.domain.interactors.track.TrackHistoryInteractor
import com.example.playlistmaker.presentation.ui.media_player.interfaces.MediaScreenState
import com.example.playlistmaker.presentation.ui.media_player.interfaces.MediaState

class MediaViewModel(
    trackHistoryInteractor: TrackHistoryInteractor,
    val mediaInteractor: MediaInteractor,
): ViewModel() {
    private var loadingTrackLiveData = MutableLiveData<MediaScreenState>()
    private var mainMediaPlayerThreadHandler: Handler? = null
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
        mainMediaPlayerThreadHandler = Handler(Looper.getMainLooper())
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
        mainMediaPlayerThreadHandler?.post(
            createUpdateTimerTask()
        )
    }

    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {
                loadingTrackLiveData.postValue(MediaScreenState.Time(mediaInteractor.currentPosition()))
                mainMediaPlayerThreadHandler?.postDelayed(this, DELAY)
            }
        }
    }

    fun pause() {
        mediaInteractor.pause()
        loadingTrackLiveData.postValue(MediaScreenState.State(MediaState.STATE_PAUSED))
        stopTimer()
    }

    private fun stopTimer() {
        mainMediaPlayerThreadHandler?.removeCallbacksAndMessages(null)
    }

    companion object {
        private const val DELAY = 500L
    }
}