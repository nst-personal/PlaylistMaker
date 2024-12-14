package com.example.playlistmaker.presentation.ui.media_player

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.data.models.Track
import com.example.playlistmaker.databinding.ActivityMediaPlayerBinding
import com.example.playlistmaker.presentation.ui.media_player.interfaces.MediaScreenState
import com.example.playlistmaker.presentation.ui.media_player.interfaces.MediaState
import com.example.playlistmaker.presentation.ui.media_player.interfaces.TrackState
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Locale

class MediaPlayerActivity : AppCompatActivity() {
    private lateinit var track: Track
    private var playerState: Int = MediaState.STATE_DEFAULT

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private val dateFormatParse by lazy { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    private lateinit var binding: ActivityMediaPlayerBinding
    private val viewModel: MediaViewModel by viewModel()

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

        viewModel.requestLoadingTrackLiveData()

        viewModel.getLoadingTrackLiveData().observe(this) { data ->
            handleData(data)
        }

        viewModel.getTrackLiveData().observe(this) {data ->
            handleTrackData(data)
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

        binding.likeMedia.setOnClickListener({
            onFavoriteClicked()
        })
    }

    fun handleTrackData(data: TrackState) {
        if (data is TrackState.Favorite) {
            track = data.track!!.copy(
                isFavorite = data.isFavorite
            )
            fillTrackContent()
        }
    }

    fun handleData(data: MediaScreenState) {
        if (data is MediaScreenState.Ready) {
            track = data.track!!
            fillContent()
        }
        if (data is MediaScreenState.Completed) {
            stopPlayer()
        }
        if (data is MediaScreenState.State) {
            playerState = data.state
        }
        if (data is MediaScreenState.Time) {
            val remainingTime = data.currentPosition
            binding.time.text = dateFormat.format(remainingTime)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stop()
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    private fun fillTrackContent() {
        if (track.isFavorite) {
            binding.likeMedia.setImageResource(R.drawable.favorite)
        } else {
            binding.likeMedia.setImageResource(R.drawable.playlist_like)
        }
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
        binding.playMedia.isEnabled = true
    }
    
    fun getCoverArtwork(url: String) = url.replaceAfterLast('/',"512x512bb.jpg")

    private fun stopPlayer() {
        playerState = MediaState.STATE_PREPARED
        binding.playMedia.setImageResource(R.drawable.playlist_play)
        binding.time.text = getResources().getString(R.string.media_player_initial_value)
    }

    private fun playbackControl() {
        when(playerState) {
            MediaState.STATE_PLAYING -> {
                pausePlayer()
            }
            MediaState.STATE_PREPARED,
            MediaState.STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun startPlayer() {
        if (track.trackTimeMillis > 0) {
            viewModel.start()
            binding.playMedia.setImageResource(R.drawable.playlist_pause)
        }
    }

    private fun pausePlayer() {
        viewModel.pause()
        binding.playMedia.setImageResource(R.drawable.playlist_play)
    }

    fun onFavoriteClicked() {
        if (this.track.isFavorite) {
            viewModel.removeTrack(this.track)
        } else {
            viewModel.addTrack(this.track)
        }
    }

}