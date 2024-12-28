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
        bottomSheetBehavior.isHideable = false

        binding.share.setOnClickListener {
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
            }
        }
    }

    private fun handlePlaylistData(data: PlaylistDetailsScreenState) {
        if (data is PlaylistDetailsScreenState.PlaylistContent) {
            playlist = data.playlist
            fillDetails()
        }
    }

    private fun fillDetails() {
        binding.playlistTitle.text = playlist?.playlistName
        binding.playlistDescription.text = playlist?.playlistDescription
        binding.tracksDuration.text = playlist?.tracksDuration?.toString() +
                " " + getResources().getString(R.string.playlist_minute)
        if (playlist?.tracksCount?.toInt() == 1) {
            binding.tracksCount.text = playlist?.tracksCount.toString() + " " +
                    getResources().getString(R.string.playlist_track)
        } else {
            binding.tracksCount.text = playlist?.tracksCount.toString() + " " +
                    getResources().getString(R.string.playlist_tracks)
        }
        if (playlist?.playlistImageUrl != null) {
            binding.pickerImage.setImageURI(playlist?.playlistImageUrl?.toUri())
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}