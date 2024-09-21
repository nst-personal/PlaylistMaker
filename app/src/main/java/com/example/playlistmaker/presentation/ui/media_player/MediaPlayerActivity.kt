package com.example.playlistmaker.presentation.ui.media_player

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.data.models.Track
import com.example.playlistmaker.databinding.ActivityMediaPlayerBinding
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Locale

class MediaPlayerActivity : AppCompatActivity() {
    private lateinit var track: Track
    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private var mainMediaPlayerThreadHandler: Handler? = null
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private val dateFormatParse by lazy { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    private lateinit var viewModel: MediaViewModel
    private lateinit var binding: ActivityMediaPlayerBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMediaPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.mediaPlayer) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(
            this, MediaViewModel.getViewModelFactory(this)
        )[MediaViewModel::class.java]

        viewModel.getLoadingTrackLiveData().observe(this) { data ->
            track = data
            fillContent()
        }

        setSupportActionBar(binding.tooltipId);

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        binding.tooltipId.setNavigationOnClickListener{
            finish()
        }

        binding.playMedia.setOnClickListener {
            playbackControl()
        }

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
        binding.authorName.text = track.artistName
        binding.trackName.text = track.trackName
        binding.countryValue.text = track.country
        binding.albumValue.text = track.collectionName
        binding.typeValue.text = track.primaryGenreName
        val date = dateFormatParse.parse(track.releaseDate)
        binding.yearValue.text = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().year.toString()
        binding.time.text = getResources().getString(R.string.media_player_initial_value)
        binding.durationValue.text = dateFormat.format(track.trackTimeMillis)

        val imageView = binding.imageTrack
        Glide.with(imageView)
            .load(getCoverArtwork(track.artworkUrl100))
            .placeholder(R.drawable.placeholder)
            .into(imageView)
        preparePlayer(track.previewUrl)
    }
    
    fun getCoverArtwork(url: String) = url.replaceAfterLast('/',"512x512bb.jpg")

    private fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            binding.playMedia.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            binding.playMedia.setImageResource(R.drawable.playlist_play)
            stopTimer()
            binding.time.text = getResources().getString(R.string.media_player_initial_value)
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
        binding.playMedia.setImageResource(R.drawable.playlist_pause)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        stopTimer()
        binding.playMedia.setImageResource(R.drawable.playlist_play)
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
                binding.time.text = dateFormat.format(remainingTime)
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