package com.example.playlistmaker.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.configuration.MediaConfig
import com.example.playlistmaker.domain.interactors.media.MediaInteractor
import com.example.playlistmaker.presentation.ui.media_player.interfaces.MediaState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

interface MediaPlayerControl {
    fun getMediaState(): StateFlow<MediaState>
    fun startPlayer()
    fun showNotification(description: String)
    fun hideNotification()
    fun pausePlayer()
}


internal class MusicService : Service(), MediaPlayerControl {
    private val mediaInteractor: MediaInteractor by inject()

    private val _playerState = MutableStateFlow<MediaState>(MediaState.Default())
    val playerState = _playerState.asStateFlow()

    private companion object {
        const val NOTIFICATION_CHANNEL_ID = "music_service_channel"
        const val SERVICE_NOTIFICATION_ID = 100
        private const val DELAY = 300L
    }

    inner class MusicServiceBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    private val binder = MusicServiceBinder()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    @SuppressLint("ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Playlist Maker",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "Service for playing music"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createServiceNotification(
        title: String = "Playlist Maker",
        description: String = "Our service is working right now!"
    ): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(description)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder {
        val songUrl = intent?.getStringExtra(MediaConfig.SONG_URL) ?: ""
        mediaInteractor.init(songUrl,
            {
                _playerState.value = MediaState.Prepared()
            },
            {
                stopTimer()
                _playerState.value = MediaState.Prepared()
            })
        return binder
    }

    private fun getForegroundServiceTypeConstant(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        } else {
            0
        }
    }

    private var timerJob: Job? = null

    override fun getMediaState(): StateFlow<MediaState> {
        return playerState
    }

    private fun stopTimer() {
        timerJob?.cancel()
    }

    override fun startPlayer() {
        mediaInteractor.start()
        startTimer()
    }

    override fun showNotification(description: String) {
        ServiceCompat.startForeground(
            this,
            SERVICE_NOTIFICATION_ID,
            createServiceNotification(description = description),
            getForegroundServiceTypeConstant()
        )
    }

    override fun hideNotification() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    private fun startTimer() {
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (mediaInteractor.isPlaying()) {
                delay(DELAY)
                _playerState.value = MediaState.Playing(mediaInteractor.currentPosition())
            }
        }
    }

    override fun pausePlayer() {
        _playerState.value = MediaState.Paused(mediaInteractor.currentPosition())
        stopTimer()
        mediaInteractor.pause()
    }

    override fun onDestroy() {
        mediaInteractor.stop()
    }

}