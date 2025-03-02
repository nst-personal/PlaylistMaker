package com.example.playlistmaker.presentation.ui.media_player

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.configuration.MediaConfig
import com.example.playlistmaker.databinding.ActivityMediaPlayerBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.media.fragments.PlaylistCreateFragment
import com.example.playlistmaker.presentation.ui.media.fragments.interfaces.playlist.PlaylistItem
import com.example.playlistmaker.presentation.ui.media.fragments.interfaces.playlist.screen.PlaylistListScreenState
import com.example.playlistmaker.presentation.ui.media_player.interfaces.MediaScreenState
import com.example.playlistmaker.presentation.ui.media_player.interfaces.MediaState
import com.example.playlistmaker.presentation.ui.media_player.interfaces.OnFragmentRemovedListener
import com.example.playlistmaker.presentation.ui.media_player.interfaces.OnPlaylistItemClickListener
import com.example.playlistmaker.presentation.ui.media_player.interfaces.TrackState
import com.example.playlistmaker.presentation.ui.search.view.adapter.PlaylistCreateAdapter
import com.example.playlistmaker.services.MusicService
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Locale


class MediaPlayerActivity : AppCompatActivity(), OnFragmentRemovedListener {
    private lateinit var track: Track
    private var playerState: MediaState = MediaState.Default()

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private val dateFormatParse by lazy { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    private lateinit var binding: ActivityMediaPlayerBinding
    private val viewModel: MediaViewModel by viewModel()
    private var musicService: MusicService? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicServiceBinder
            musicService = binder.getService()
            viewModel.setAudioPlayerControl(binder.getService())
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
        }
    }

    private var playlist: List<Playlist>? = null

    private var adapter: PlaylistCreateAdapter? = null

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            bindMusicService()
        } else {
            Toast.makeText(this, "Cannot bind service!", Toast.LENGTH_LONG).show()
        }
    }

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

        viewModel.getTrackLiveData().observe(this) { data ->
            handleTrackData(data)
        }

        viewModel.observePlayerState().observe(this) {
            playerState = it
            updateButtonAndProgress()
            updateNotificationState()
        }

        setSupportActionBar(binding.tooltipId);

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        binding.tooltipId.setNavigationOnClickListener {
            finish()
        }

        binding.playMedia.setOnPlayPauseButtonClickListener(object :
            OnPlaybackButtonViewClickListener {
            override fun onTouch(isPlaying: Boolean) {
                playbackControl()
            }
        })

        binding.likeMedia.setOnClickListener({
            onFavoriteClicked()
        })

        viewModel.getLoadingPlaylistLiveData().observe(this) { data ->
            handlePlaylistData(data)
        }

        val bottomSheetContainer = findViewById<LinearLayout>(R.id.standard_bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)

        handleSaveToPlaylistView()

        viewModel.getPlaylistStateLiveData().observe(this) { data ->
            if (data) {
                openFragment()
            } else {
                removeFragment()
            }
        }
    }

    private fun updateNotificationState() {
        if (playerState is MediaState.Prepared ||
            playerState is MediaState.Paused) {
            viewModel.hideNotification()
        }

    }

    private fun updateButtonAndProgress() {
        if (playerState is MediaState.Prepared ||
            playerState is MediaState.Paused) {
            binding.playMedia.updateState(false)
        }
        val remainingTime = playerState.progress
        binding.time.text = dateFormat.format(remainingTime)
    }

    private fun openFragment() {
        binding.fragmentContainer.visibility = View.VISIBLE
        binding.mediaPlayerActivity.visibility = View.GONE
        binding.blurContainer.visibility = View.GONE
        binding.standardBottomSheet.visibility = View.GONE
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        binding.container.apply {
            (layoutParams as ViewGroup.MarginLayoutParams).apply {
                topMargin = resources.getDimensionPixelSize(R.dimen.margin_container_top)
            }
            requestLayout()
        }
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (fragment == null) {
            val createPlaylistFragment = PlaylistCreateFragment()
            val fragmentManager: FragmentManager = supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, createPlaylistFragment)
            fragmentTransaction.commit()
        }
    }

    private fun handlePlaylistData(data: PlaylistListScreenState) {
        if (data is PlaylistListScreenState.PlaylistListContent) {
            playlist = data.playlists
            handlePlaylistView()
        }
        if (data is PlaylistListScreenState.PlaylistListUpdatedContent) {
            Toast.makeText(
                this,
                "${getString(R.string.playlist_tracks_added)} ${data.playlist.playlistName}",
                Toast.LENGTH_SHORT
            ).show()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        if (data is PlaylistListScreenState.PlaylistListNotUpdatedContent) {
            Toast.makeText(
                this,
                "${getString(R.string.playlist_tracks_already_added)} ${data.playlist.playlistName}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handlePlaylistView() {
        val playlistClickListener = object : OnPlaylistItemClickListener {
            override fun onItemClick(playlist: PlaylistItem) {
                viewModel.updatePlaylist(track,
                    this@MediaPlayerActivity.playlist?.first { item -> item.playlistId == playlist.id }!!
                )
            }
        }
        if (playlist?.isEmpty() == false) {
            binding.playlist.layoutManager = LinearLayoutManager(this)
            binding.playlist.isClickable = true
            adapter = PlaylistCreateAdapter(
                playlist!!.map { item ->
                    var file: File? = null
                    if (item.playlistImageUrl?.isNotEmpty() == true) {
                        val filePath = File(
                            applicationContext?.getExternalFilesDir(
                                Environment.DIRECTORY_PICTURES
                            ), "myalbum"
                        )
                        file = File(filePath, item.playlistImageUrl?.substringAfterLast("/"))
                    }
                    PlaylistItem(item.playlistId, item.playlistName, item.playlistTracksCount, file)
                }, playlistClickListener)
            binding.playlist.adapter = adapter
            binding.playlist.isVisible = true
        } else {
            binding.playlist.isVisible = false
        }
    }

    private fun handleSaveToPlaylistView() {
        this.bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        binding.blurContainer.visibility = View.GONE

        binding.addMedia.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            binding.blurContainer.visibility = View.VISIBLE
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        binding.blurContainer.visibility = View.VISIBLE
                        loadPlaylist()
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.blurContainer.visibility = View.GONE
                    }

                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.blurContainer.visibility = View.GONE
                    }

                    else -> {
                        binding.blurContainer.visibility = View.GONE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.addNew.setOnClickListener {
            viewModel.openPlaylist()
        }
    }

    private fun loadPlaylist() {
        viewModel.showPlaylist()
    }

    fun handleTrackData(data: TrackState) {
        if (data is TrackState.Favorite) {
            track = data.track!!.copy(
                isFavorite = data.isFavorite
            )
            fillTrackContent()
        }
    }

    fun handleService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            bindMusicService()
        }
    }

    fun handleData(data: MediaScreenState) {
        if (data is MediaScreenState.Ready) {
            track = data.track!!
            fillContent()
            handleService()
        }
        if (data is MediaScreenState.Completed) {
            stopPlayer()
        }
        if (data is MediaScreenState.State) {
            playerState = data.state
        }
    }

    override fun onDestroy() {
        unbindMusicService()
        super.onDestroy()
    }

    private fun bindMusicService() {
        val intent = Intent(this, MusicService::class.java).apply {
            putExtra(MediaConfig.SONG_URL, track.previewUrl)
        }
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun unbindMusicService() {
        unbindService(serviceConnection)
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
        binding.yearValue.text =
            date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().year.toString()
        binding.time.text = getResources().getString(R.string.media_player_initial_value)
        binding.durationValue.text = dateFormat.format(track.trackTimeMillis)

        val imageView = binding.imageTrack
        Glide.with(imageView)
            .load(getCoverArtwork(track.artworkUrl100))
            .placeholder(R.drawable.placeholder)
            .into(imageView)
        binding.playMedia.isEnabled = true
    }

    fun getCoverArtwork(url: String) = url.replaceAfterLast('/', "512x512bb.jpg")

    private fun stopPlayer() {
        playerState = MediaState.Prepared()
        binding.playMedia.updateState()
        binding.time.text = getResources().getString(R.string.media_player_initial_value)
    }

    private fun playbackControl() {
        if (playerState is MediaState.Prepared ||
            playerState is MediaState.Paused) {
            if (track.trackTimeMillis > 0) {
                binding.playMedia.updateState()
                viewModel.startPlayer()
            }
        } else {
            binding.playMedia.updateState()
            viewModel.pausePlayer()
        }
    }

    fun onFavoriteClicked() {
        if (this.track.isFavorite) {
            viewModel.removeTrack(this.track)
        } else {
            viewModel.addTrack(this.track)
        }
    }

    private fun removeFragment() {
        binding.fragmentContainer.visibility = View.GONE
        binding.mediaPlayerActivity.visibility = View.VISIBLE
        binding.blurContainer.visibility = View.GONE
        binding.standardBottomSheet.visibility = View.VISIBLE
        binding.container.apply {
            (layoutParams as ViewGroup.MarginLayoutParams).apply {
                topMargin = resources.getDimensionPixelSize(R.dimen.margin_container_top_empty)
            }
            requestLayout()
        }
    }

    override fun onFragmentRemoved() {
        viewModel.closePlaylist()
    }

    override fun onPause() {
        if (playerState is MediaState.Playing) {
            viewModel.showNotification("${track.artistName} - ${track.trackName}")
        }
        super.onPause()
    }

    override fun onResume() {
        viewModel.hideNotification()
        super.onResume()
    }

}