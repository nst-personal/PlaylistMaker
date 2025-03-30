package com.example.playlistmaker.presentation.ui.media

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.media.fragments.FavoriteViewModel
import com.example.playlistmaker.presentation.ui.media.fragments.PlaylistListViewModel
import com.example.playlistmaker.presentation.ui.media.functions.MediaScreen
import com.example.playlistmaker.presentation.ui.media_player.MediaPlayerActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaFragment : Fragment() {
    private lateinit var binding: FragmentMediaBinding
    private val viewModel: FavoriteViewModel by viewModel()
    private val playlistListViewModel: PlaylistListViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun openMediaPlayer(track: Track) {
        viewModel.saveTrack(track)
        val displayMediaIntent = Intent(requireContext(), MediaPlayerActivity::class.java)
        startActivity(displayMediaIntent)
    }

    override fun onResume() {
        super.onResume()
        viewModel.showFavorites()
        playlistListViewModel.showPlaylist()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(binding.media) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.composeView.setContent {
            MediaScreen(viewModel, playlistListViewModel, { track ->
                openMediaPlayer(track)
            }, {
                findNavController().navigate(R.id.action_media_to_playlistCreateFragment3)
            }, { playlistItem ->
                val bundle = Bundle().apply {
                    putLong("playlistId", playlistItem.playlistId)
                }
                findNavController().navigate(R.id.action_media_to_playListDetailsFragment, bundle)
            })
        }
    }

}
