package com.example.playlistmaker.presentation.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.playlistmaker.presentation.ui.media_player.MediaPlayerActivity
import com.example.playlistmaker.presentation.ui.search.functions.SearchScreen
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var searchValue: String = ""

    private val viewModel: SearchViewModel by viewModel()

    override fun onResume() {
        super.onResume()
        viewModel.showHistory()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            searchValue = savedInstanceState.getString(SEARCH, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                SearchScreen(viewModel = viewModel, openMediaPlayer = { track ->
                    viewModel.saveTrack(track)
                    val displayMediaIntent = Intent(requireContext(), MediaPlayerActivity::class.java)
                    startActivity(displayMediaIntent)
                }, {
                    viewModel.clearHistory()
                    viewModel.showHistory()
                })
            }
        }
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
