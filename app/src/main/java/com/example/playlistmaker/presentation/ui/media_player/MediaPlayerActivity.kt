package com.example.playlistmaker.presentation.ui.media_player

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.data.models.Track
import com.example.playlistmaker.domain.creators.track.TrackHistoryCreator
import com.example.playlistmaker.domain.managers.track.TrackHistoryManager
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerActivity : AppCompatActivity() {
    private lateinit var trackHistoryManager: TrackHistoryManager
    private lateinit var play: ImageView
    private lateinit var track: Track
    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private var mainMediaPlayerThreadHandler: Handler? = null
    private var timeLeftTextView: TextView? = null
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_media_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mediaPlayer)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        trackHistoryManager = TrackHistoryCreator().provideTrackHistoryManager(this)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.tooltipId)
        setSupportActionBar(toolbar);

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener{
            finish()
        }
        play = findViewById(R.id.playMedia)
        fillContent()

        play.setOnClickListener {
            playbackControl()
        }

        timeLeftTextView = findViewById(R.id.time)
        mainMediaPlayerThreadHandler = Handler(Looper.getMainLooper())
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    private fun fillContent() {
        track = trackHistoryManager.findLast()
        setText(R.id.authorName, track.artistName)
        setText(R.id.trackName, track.trackName)
        setText(R.id.countryValue, track.country)
        setText(R.id.albumValue, track.collectionName)
        setText(R.id.typeValue, track.primaryGenreName)
        setText(R.id.yearValue, track.releaseDate)
        setText(R.id.time, getResources().getString(R.string.media_player_initial_value))
        setText(R.id.durationValue, dateFormat.format(track.trackTimeMillis))

        val imageView = findViewById<com.google.android.material.imageview.ShapeableImageView>(R.id.imageTrack)
        Glide.with(imageView)
            .load(getCoverArtwork(track.artworkUrl100))
            .placeholder(R.drawable.placeholder)
            .into(imageView)
        preparePlayer(track.previewUrl)
    }
    private fun setText(id: Int, text: String) {
        val view = findViewById<TextView>(id)
        view.setText(text)
    }

    fun getCoverArtwork(url: String) = url.replaceAfterLast('/',"512x512bb.jpg")

    private fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            play.setImageResource(R.drawable.playlist_play)
            stopTimer()
            timeLeftTextView?.text = getResources().getString(R.string.media_player_initial_value)
        }
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        startTimer()
        play.setImageResource(R.drawable.playlist_pause)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        stopTimer()
        play.setImageResource(R.drawable.playlist_play)
    }

    private fun stopTimer() {
        mainMediaPlayerThreadHandler?.removeCallbacksAndMessages(null)
    }

    private fun startTimer() {
        mainMediaPlayerThreadHandler?.post(
            createUpdateTimerTask()
        )
    }

    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {
                val remainingTime = mediaPlayer.currentPosition
                timeLeftTextView?.text = dateFormat.format(remainingTime)
                mainMediaPlayerThreadHandler?.postDelayed(this, DELAY)
            }
        }
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 500L
    }
}