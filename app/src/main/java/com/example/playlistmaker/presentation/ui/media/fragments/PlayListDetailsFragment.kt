package com.example.playlistmaker.presentation.ui.media.fragments

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.media.fragments.adapter.PlaylistTrackAdapter
import com.example.playlistmaker.presentation.ui.media.fragments.interfaces.playlist.screen.PlaylistDetailsScreenState
import com.example.playlistmaker.presentation.ui.media_player.MediaPlayerActivity
import com.example.playlistmaker.presentation.ui.media_player.interfaces.OnPlaylistTrackItemClickListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayListDetailsFragment : Fragment() {

    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistDetailsViewModel by viewModel()

    private var playlist: Playlist? = null
    private var playlistId: Long? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetMoreBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlistId = arguments?.getLong("playlistId")
        viewModel.getLoadingPlaylistLiveData().observe(viewLifecycleOwner) { data ->
            handlePlaylistData(data)
        }
        binding.toolbarId.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        bottomSheetBehavior = BottomSheetBehavior.from(binding.trackList)
        binding.blurContainer.visibility = View.GONE
        bottomSheetMoreBehavior = BottomSheetBehavior.from(binding.playlistMore).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        bottomSheetMoreBehavior.setPeekHeight(getResources().getDisplayMetrics().heightPixels / 2)
        bottomSheetBehavior.isHideable = false


        binding.moreShare.setOnClickListener{
            handleShare()
        }

        binding.share.setOnClickListener {
            handleShare()
        }

        binding.moreDelete.setOnClickListener{
            handleDeletion()
        }

        binding.moreEdit.setOnClickListener{
            val bundle = Bundle().apply {
                putLong("playlistId", playlistId!!)
            }
            findNavController().navigate(R.id.action_playListDetailsFragment_to_playlistEditFragment, bundle)
        }

        binding.more.setOnClickListener {
            bottomSheetMoreBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetMoreBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        binding.blurContainer.visibility = View.VISIBLE
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.blurContainer.visibility = View.VISIBLE
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
    }

    private fun handleDeletion() {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.playlist_delete_confirmation_title))
            .setMessage(getString(R.string.playlist_delete_confirmation))
            .setPositiveButton(getString(R.string.playlist_track_delete_confirmation_yes)) { dialog, which ->
                viewModel.deletePlaylist(playlist!!)
                findNavController().popBackStack()
            }
            .setNegativeButton(getString(R.string.playlist_track_delete_confirmation_no)) { dialog, which ->

            }.show()
    }

    private fun handleShare() {
        if (playlist?.tracks?.isEmpty() == true) {
            MaterialAlertDialogBuilder(requireActivity())
                .setMessage(getString(R.string.playlist_track_share_info))
                .setNeutralButton(
                    getString(R.string.playlist_track_share_info_ok),
                    object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {

                        }
                    })
                .show()
        } else {
            sendContent()
        }
    }

    private fun sendContent() {
        val trackCount = playlist?.tracks?.size.toString() + " " +
                getString(R.string.playlist_tracks) + "\n"
        val trackListString =
            playlist?.tracks?.mapIndexed { index, track ->
                "${index + 1}. " +
                        "${track.artistName} - ${track.trackName} (${
                            (track.trackTimeMillis / 60000).toString() +
                                    getString(R.string.playlist_minute)
                        })"
            }
        val content = StringBuilder()
        content.append(trackCount)
        content.append(trackListString?.joinToString("\n"))
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.setType("text/plain")
        sendIntent.putExtra(Intent.EXTRA_TEXT, content.toString())
        startActivity(Intent.createChooser(sendIntent, null))
    }

    private fun handlePlaylistData(data: PlaylistDetailsScreenState) {
        if (data is PlaylistDetailsScreenState.PlaylistContent) {
            playlist = data.playlist
            fillDetails()
        }
    }

    private fun fillDetails() {
        binding.playlistTitle.text = playlist?.playlistName
        binding.playlistName.text = playlist?.playlistName
        binding.playlistDescription.text = playlist?.playlistDescription
        binding.tracksDuration.text = playlist?.tracksDuration?.toString() +
                " " + getResources().getString(R.string.playlist_minute)
        if (playlist?.tracksCount?.toInt() == 1) {
            binding.tracksCount.text = playlist?.tracksCount.toString() + " " +
                    getResources().getString(R.string.playlist_track)
            binding.playlistTracksCount.text = playlist?.tracksCount.toString() + " " +
                    getResources().getString(R.string.playlist_track)
        } else {
            binding.tracksCount.text = playlist?.tracksCount.toString() + " " +
                    getResources().getString(R.string.playlist_tracks)
            binding.playlistTracksCount.text = playlist?.tracksCount.toString() + " " +
                    getResources().getString(R.string.playlist_tracks)
        }
        if (playlist?.playlistImageUrl != null) {
            binding.pickerImage.setImageURI(playlist?.playlistImageUrl?.toUri())
            binding.playlistImage.setImageURI(playlist?.playlistImageUrl?.toUri())
        }
        val trackClickListener = object : OnPlaylistTrackItemClickListener {
            override fun onItemClick(track: Track) {
                viewModel.saveTrack(track)
                val displayMediaIntent = Intent(requireContext(), MediaPlayerActivity::class.java)
                startActivity(displayMediaIntent)
            }

            override fun onItemLongClick(playlistTrack: Track) {
                MaterialAlertDialogBuilder(requireActivity())
                    .setMessage(getString(R.string.playlist_track_delete_confirmation))
                    .setPositiveButton(getString(R.string.playlist_track_delete_confirmation_yes)) { dialog, which ->
                        viewModel.deleteTrack(playlist!!, playlistTrack)
                    }
                    .setNegativeButton(getString(R.string.playlist_track_delete_confirmation_no)) { dialog, which ->

                    }.show()
            }
        }
        binding.playlistTracks.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistTracks.isClickable = true
        binding.playlistTracks.adapter =
            PlaylistTrackAdapter(playlist?.tracks!!, trackClickListener)


    }

    override fun onResume() {
        super.onResume()
        viewModel.showPlaylist(playlistId!!)
        if (bottomSheetMoreBehavior.state !== BottomSheetBehavior.STATE_HIDDEN) {
            binding.blurContainer.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}