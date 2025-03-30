package com.example.playlistmaker.presentation.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.media_player.MediaPlayerActivity
import com.example.playlistmaker.presentation.ui.search.functions.SearchScreen
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var binding: FragmentSearchBinding? = null

    private var searchValue: String = ""

    private val viewModel: SearchViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.showHistory()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            searchValue = savedInstanceState.getString(SEARCH, "")
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding?.search!!) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding?.composeView?.setContent {
            SearchScreen(viewModel = viewModel, openMediaPlayer = { track ->
                this.openMediaPlayer(track)
            }, {
                this.clearHistory()
            })
        }
    }

    private fun clearHistory() {
        viewModel.clearHistory()
        viewModel.showHistory()
    }

    private fun openMediaPlayer(track: Track) {
        viewModel.saveTrack(track)
        val displayMediaIntent = Intent(requireContext(), MediaPlayerActivity::class.java)
        startActivity(displayMediaIntent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH, searchValue)
    }

    private companion object {
        const val SEARCH = "SEARCH"
        const val ITEM_BUTTON_DEBOUNCE_DELAY = 1000L
    }
}
