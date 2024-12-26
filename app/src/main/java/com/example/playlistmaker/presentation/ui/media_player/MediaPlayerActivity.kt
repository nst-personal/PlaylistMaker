package com.example.playlistmaker.presentation.ui.media_player

import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.databinding.ActivityMediaPlayerBinding
import com.example.playlistmaker.presentation.ui.media.fragments.PlaylistCreateFragment
import com.example.playlistmaker.presentation.ui.media.fragments.interfaces.playlist.PlaylistItem
import com.example.playlistmaker.presentation.ui.media.fragments.interfaces.playlist.screen.PlaylistListScreenState
import com.example.playlistmaker.presentation.ui.media_player.interfaces.MediaScreenState
import com.example.playlistmaker.presentation.ui.media_player.interfaces.MediaState
import com.example.playlistmaker.presentation.ui.media_player.interfaces.OnFragmentRemovedListener
import com.example.playlistmaker.presentation.ui.media_player.interfaces.OnPlaylistItemClickListener
import com.example.playlistmaker.presentation.ui.media_player.interfaces.TrackState
import com.example.playlistmaker.presentation.ui.search.view.adapter.PlaylistCreateAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Locale


class MediaPlayerActivity : AppCompatActivity(), OnFragmentRemovedListener {
    private lateinit var track: Track
    private var playerState: Int = MediaState.STATE_DEFAULT

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private val dateFormatParse by lazy { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    private lateinit var binding: ActivityMediaPlayerBinding
    private val viewModel: MediaViewModel by viewModel()

    private var playlist: List<Playlist>? = null

    private var adapter: PlaylistCreateAdapter? = null

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

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

        viewModel.getLoadingPlaylistLiveData().observe(this) { data ->
            handlePlaylistData(data)
        }

        val bottomSheetContainer = findViewById<LinearLayout>(R.id.standard_bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)

        handleSaveToPlaylistView()

        viewModel.getPlaylistStateLiveData().observe(this){
                data ->
            if (data) {
                openFragment()
            } else {
                removeFragment()
            }
        }
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
                "${getString(R.string.playlist_tracks_added)} ${data.playlist.playlistName}", Toast.LENGTH_SHORT
            ).show()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        if (data is PlaylistListScreenState.PlaylistListNotUpdatedContent) {
            Toast.makeText(
                this,
                "${getString(R.string.playlist_tracks_already_added)} ${data.playlist.playlistName}", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handlePlaylistView() {
        val playlistClickListener = object : OnPlaylistItemClickListener {
            override fun onItemClick(playlist: PlaylistItem) {
                viewModel.updatePlaylist(track,
                    this@MediaPlayerActivity.playlist?.first { item -> item.playlistId == playlist.id }!!)
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
                    }

                , playlistClickListener)
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

}