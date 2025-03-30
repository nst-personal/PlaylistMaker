package com.example.playlistmaker.presentation.ui.search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.media_player.MediaPlayerActivity
import com.example.playlistmaker.presentation.ui.search.interfaces.OnTrackItemClickListener
import com.example.playlistmaker.presentation.ui.search.interfaces.TrackScreenState
import com.example.playlistmaker.presentation.ui.search.view.adapter.TrackAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class SearchFragment : Fragment() {
    private var binding: FragmentSearchBinding? = null

    private var historyView: RecyclerView? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: TrackAdapter? = null
    private var searchProgressBar: ProgressBar? = null

    private var searchValue: String = ""

    private var tracks = listOf<Track>()
    private var historyTracks = listOf<Track>()
    private val viewModel: SearchViewModel by viewModel()
    private var isItemClickAllowed = true

    private fun clickItemDebounce() : Boolean {
        val current = isItemClickAllowed
        if (isItemClickAllowed) {
            isItemClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(ITEM_BUTTON_DEBOUNCE_DELAY)
                isItemClickAllowed = true
            }
        }
        return current
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
        binding?.inputEditText?.setText(searchValue)
        ViewCompat.setOnApplyWindowInsetsListener(binding?.search!!) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        searchProgressBar = binding?.searchProgressBarId

        val clearButton = binding?.clearIcon

        clearButton?.setOnClickListener {
            binding?.inputEditText?.setText("")
            handleHistoryView()
            viewModel.showHistory()
            searchProgressBar?.isVisible = false
        }

        recyclerView = binding?.tracksList
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        recyclerView?.isClickable = true

        historyView = binding?.historyTracksList
        historyView?.layoutManager = LinearLayoutManager(requireContext())
        historyView?.isClickable = true

        binding?.inputEditText?.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                handleHistoryView()
            }
        }

        val clearHistoryButton = binding?.clearHistory
        clearHistoryButton?.setOnClickListener {
            viewModel.clearHistory()
            showHistory(false)
            searchProgressBar?.isVisible = false
        }

        viewModel.getLoadingTrackLiveData().observe(viewLifecycleOwner) { screenState ->
            when (screenState) {
                is TrackScreenState.SearchContent -> {
                    if (screenState.tracks == null) {
                        showSearchErrorView(true, screenState.search)
                        showSearchNotFoundView(false)
                        showHistory(false)
                        recyclerView?.isVisible = false
                    } else {
                        handleTrackData(screenState.tracks, screenState.search)
                    }
                }
                is TrackScreenState.HistoryContent -> {
                    val trackClickListener = object : OnTrackItemClickListener {
                        override fun onItemClick(track: Track) {
                            openMediaPlayer(track)
                        }
                    }
                    historyTracks = screenState.tracks
                    historyView?.adapter = TrackAdapter(historyTracks, trackClickListener)
                }
            }
        }

        binding?.composeView?.setContent {
            SearchScreen(viewModel = viewModel)
        }
    }

    private fun openMediaPlayer(track: Track) {
        if (clickItemDebounce()) {
            viewModel.saveTrack(track)
            val displayMediaIntent = Intent(requireContext(), MediaPlayerActivity::class.java)
            startActivity(displayMediaIntent)
        }
    }

    private fun handleHistoryView() {
        recyclerView?.isVisible = false
        showHistory(historyTracks.isNotEmpty() && searchValue.isEmpty())
    }

    private fun handleSearchTracks(savedSearchValue: String) {
        if (savedSearchValue.isNotEmpty()) {
            viewModel.searchTracks(savedSearchValue)
        }
    }

    private fun handleTrackData(resultList: List<Track>?, savedSearchValue: String) {
        showSearchErrorView(false, savedSearchValue)
        if (resultList != null) {
            searchProgressBar?.isVisible = false
            if (resultList.isNotEmpty()) {
                tracks = resultList
                val trackClickListener = object : OnTrackItemClickListener {
                    override fun onItemClick(track: Track) {
                        viewModel.addTrack(track)
                        openMediaPlayer(track)
                    }
                }
                adapter = TrackAdapter(tracks, trackClickListener)
                recyclerView?.adapter = adapter
                recyclerView?.isVisible = true
                showSearchNotFoundView(false)
                showHistory(false)
            } else {
                showSearchNotFoundView(true)
                showHistory(false)
                recyclerView?.isVisible = false
            }
        } else {
            showSearchErrorView(true, savedSearchValue)
            showSearchNotFoundView(false)
            showHistory(false)
            recyclerView?.isVisible = false
        }
    }

    private fun showHistory(isVisible: Boolean) {
        if (isVisible) {
            viewModel.showHistory()
        }
        binding?.historyData?.isVisible = isVisible
    }

    private fun showSearchNotFoundView(isVisible: Boolean) {
        val searchNoDataTextView = binding?.searchNoDataText
        val searchNoDataImageView = binding?.searchNoDataIcon
        searchNoDataTextView?.isVisible = isVisible
        searchNoDataImageView?.isVisible = isVisible
        searchProgressBar?.isVisible = false
    }

    private fun showSearchErrorView(isVisible: Boolean, savedSearchValue: String) {
        val searchErrorTextView = binding?.searchErrorText
        val searchErrorConnectionTextView = binding?.searchErrorTextConnection
        val searchErrorImageView = binding?.searchErrorIcon
        val retryButton = binding?.retry
        searchErrorTextView?.isVisible = isVisible
        searchErrorTextView?.isVisible = isVisible
        searchErrorConnectionTextView?.isVisible = isVisible
        searchErrorImageView?.isVisible = isVisible
        retryButton?.isVisible = isVisible
        retryButton?.setOnClickListener{
            handleSearchTracks(savedSearchValue)
        }
        searchProgressBar?.isVisible = false
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

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SearchScreen(viewModel: SearchViewModel) {
    var query by remember { mutableStateOf("") }
    var isClearIconVisible = remember { false }
    var historyView by remember { mutableStateOf(null) }
    var recyclerView by remember { mutableStateOf(null) }
    var searchProgressBar by remember { mutableStateOf(false) }
    var tracks by remember { mutableStateOf<List<Track>>(listOf()) }
    var historyTracks by remember { mutableStateOf<List<Track>>(listOf()) }
    var historyData = remember { false }


    val screenState by viewModel.getLoadingTrackLiveData().asFlow().collectAsState(initial = null)

    when (screenState) {
        is TrackScreenState.SearchContent -> {
            val state = screenState as TrackScreenState.SearchContent
            if (state.tracks == null) {
//                showSearchErrorView(true, state.search)
//                showSearchNotFoundView(false)
//                showHistory(false)
//                recyclerView?.isVisible = false
            } else {
                tracks = state.tracks
                searchProgressBar = false
//                handleTrackData(state.tracks, state.search)
            }
        }
        is TrackScreenState.HistoryContent -> {
//            val trackClickListener = object : OnTrackItemClickListener {
//                override fun onItemClick(track: Track) {
//                    openMediaPlayer(track)
//                }
//            }
            historyTracks = (screenState as TrackScreenState.HistoryContent).tracks
        }
        null -> {}
    }


    fun showHistory(isVisible: Boolean) {
        if (isVisible) {
            viewModel.showHistory()
        }
        historyData = isVisible
    }

    fun showSearchNotFoundView(isVisible: Boolean) {
//        val searchNoDataTextView = binding?.searchNoDataText
//        val searchNoDataImageView = binding?.searchNoDataIcon
//        searchNoDataTextView?.isVisible = isVisible
//        searchNoDataImageView?.isVisible = isVisible
        searchProgressBar = false
    }

    fun showSearchErrorView(isVisible: Boolean, savedSearchValue: String) {
//        val searchErrorTextView = binding?.searchErrorText
//        val searchErrorConnectionTextView = binding?.searchErrorTextConnection
//        val searchErrorImageView = binding?.searchErrorIcon
//        val retryButton = binding?.retry
//        searchErrorTextView?.isVisible = isVisible
//        searchErrorTextView?.isVisible = isVisible
//        searchErrorConnectionTextView?.isVisible = isVisible
//        searchErrorImageView?.isVisible = isVisible
//        retryButton?.isVisible = isVisible
//        retryButton?.setOnClickListener{
//            handleSearchTracks(savedSearchValue)
//        }
        searchProgressBar = false
    }

    fun onTextChanged(queryValue: String) {
        query = queryValue
        val isVisible = queryValue.isNotEmpty()
        isClearIconVisible = isVisible
        showHistory(false)
        showSearchNotFoundView(false)
        showSearchErrorView(false, "")
        recyclerView?.isVisible = false
        searchProgressBar = isVisible
        viewModel.searchDebounce(
            changedText = query
        )
    }
    fun handleHistoryView() {
        recyclerView?.isVisible = false
        showHistory(historyTracks.isNotEmpty() && query.isEmpty())
    }

    fun clearText() {
        println("Clear")
        query = ""
        handleHistoryView()
        viewModel.showHistory()
        searchProgressBar = false
    }

    fun onFocus(hasFocus: Boolean) {
        println(hasFocus)
        if (hasFocus) {
            handleHistoryView()
        }
    }

    Scaffold(
        topBar = { Toolbar() },
        backgroundColor = Color.Transparent
    ) {

        Spacer(modifier = Modifier.height(16.dp))
        SearchInput(
            query = query,
            onQueryChanged = { newQuery -> onTextChanged(newQuery) },
            onClearClick = { clearText() },
            onFocus = { hasFocus -> onFocus(hasFocus) },
            isClearIconVisible = isClearIconVisible
        )

        if (searchProgressBar) {
            Spacer(modifier = Modifier.height(16.dp))
            SearchProgressBar(isVisible = true)
        }

        if (tracks.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            TracksList(tracks = tracks)
        }

//        Spacer(modifier = Modifier.height(16.dp))
//
//        // No Data Content
//        NoDataContent(isVisible = true)
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Error Content
//        ErrorContent(isVisible = true, onRetryClick = {})
//
//        Spacer(modifier = Modifier.height(16.dp))
//

        if (historyData) {
            HistoryContent(history = historyTracks, isVisible = historyData, onClearHistoryClick = {})
        }
    }

}

@Composable
fun Toolbar() {
    val textColor = if (isSystemInDarkTheme()) {
        colorResource(id = R.color.white)
    } else {
        colorResource(id = R.color.black)
    }

    Row {
        Column( modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(id = R.dimen.main_top_padding),
                vertical = dimensionResource(id = R.dimen.main_top_padding),
            )
            .background(color = Color.Transparent)) {
            Text(
                color = textColor,
                fontWeight = FontWeight(500),
                text = stringResource(id = R.string.search_main_header),
                fontSize = dimensionResource(id = R.dimen.search_main_title).value.sp
            )
        }
    }
}

@Composable
fun SearchInput(
    query: String,
    onQueryChanged: (String) -> Unit,
    onClearClick: () -> Unit,
    onFocus: (Boolean) -> Unit,
    isClearIconVisible: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.main_top_padding))
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChanged,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { isFocus -> onFocus(isFocus.hasFocus) }
                .background(colorResource(id = R.color.textbox), RoundedCornerShape(16.dp)),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.search),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            trailingIcon = {
                if (isClearIconVisible) {
                    IconButton(onClick = {
                        onClearClick()
                    },
                        modifier = Modifier
                            .clickable{
                                onClearClick()
                            }) {
                        Icon(
                            painter = painterResource(id = R.drawable.clear),
                            contentDescription = null,
                            modifier = Modifier
                                .clickable{
                                    onClearClick()
                                }
                        )
                    }
                }
            },
            placeholder = { Text(text = stringResource(id = R.string.search)) },
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = colorResource(id = R.color.blue)
            ),
            maxLines = 1
        )
    }
}
@Composable
fun SearchProgressBar(isVisible: Boolean) {
    if (isVisible) {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.search_progress_bar))
                    .padding(top = dimensionResource(id = R.dimen.search_progress_bar_margin)),
                color = colorResource(id = R.color.search_cursor_color)
            )
        }
    }
}

@Composable
fun TracksList(tracks: List<Track>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(id = R.dimen.view_padding),
                vertical  = dimensionResource(id = R.dimen.scroll_search_container_margin)
            )
    ) {
        items(tracks) { track ->
            TrackItem(track = track)
        }
    }
}

@Composable
fun TrackItem(
    track: Track
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = dimensionResource(id = R.dimen.item_top_padding),
                bottom = dimensionResource(id = R.dimen.item_top_padding)
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = track.artworkUrl100,
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.placeholder),
            error = painterResource(id = R.drawable.placeholder)
        )

        Spacer(modifier = Modifier.width(16.dp))

        TrackInfo(
            track = track,
        )

        Spacer(modifier = Modifier.width(16.dp))

        Image(
            painter = painterResource(id = R.drawable.forward),
            contentDescription = "",
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun TrackInfo(
    track: Track,
) {
    val textColor = if (isSystemInDarkTheme()) {
        colorResource(id = R.color.white)
    } else {
        colorResource(id = R.color.black)
    }

    Column{
        Text(
            color = textColor,
            text = track.trackName,
            style = MaterialTheme.typography.body1.copy(
                fontWeight = FontWeight.Bold
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                color = textColor,
                text = track.artistName,
                style = MaterialTheme.typography.body2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(0.75f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Image(
                painter = painterResource(id = R.drawable.point),
                contentDescription = null,
                modifier = Modifier.size(4.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                color = textColor,
                text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis),
                style = MaterialTheme.typography.body2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun PlaylistCard(
    playlistImage: Int,
    title: String,
    size: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = dimensionResource(id = R.dimen.cardview_bottom)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = playlistImage),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Text(
            text = title,
            style = MaterialTheme.typography.h6.copy(
                fontSize = dimensionResource(id = R.dimen.playlist_font_size_title).value.sp,
                fontWeight = FontWeight.Bold
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 14.dp)
        )

        Text(
            text = size,
            style = MaterialTheme.typography.subtitle1.copy(
                fontSize = dimensionResource(id = R.dimen.playlist_font_size_track_size).value.sp,
                fontWeight = FontWeight.Bold
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

@Composable
fun NoDataContent(isVisible: Boolean) {
    if (isVisible) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dimensionResource(id = R.dimen.main_top_padding))
        ) {
            Image(
                painter = painterResource(id = R.drawable.not_found),
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_issue))
            )
            Text(
                text = stringResource(id = R.string.search_not_found),
                modifier = Modifier.padding(top = dimensionResource(id = R.dimen.margin_top))
            )
        }
    }
}

@Composable
fun ErrorContent(isVisible: Boolean, onRetryClick: () -> Unit) {
    if (isVisible) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dimensionResource(id = R.dimen.main_top_padding))
        ) {
            Image(
                painter = painterResource(id = R.drawable.no_internet),
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_issue))
            )
            Text(
                text = stringResource(id = R.string.search_error),
                modifier = Modifier.padding(top = dimensionResource(id = R.dimen.settings_description_top))
            )
            Text(
                text = stringResource(id = R.string.search_error_check_connection),
                modifier = Modifier.padding(top = dimensionResource(id = R.dimen.sub_description))
            )
            Button(
                onClick = onRetryClick,
                modifier = Modifier
                    .padding(
                        start = dimensionResource(id = R.dimen.button_margin),
                        end = dimensionResource(id = R.dimen.button_margin),
                        top = dimensionResource(id = R.dimen.button_margin_bottom)
                    )
            ) {
                Text(text = stringResource(id = R.string.search_retry))
            }
        }
    }
}

@Composable
fun HistoryContent(
    history: List<Track>,
    isVisible: Boolean,
    onClearHistoryClick: () -> Unit
) {
    if (isVisible) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dimensionResource(id = R.dimen.main_top_padding))
        ) {
            Text(
                text = stringResource(id = R.string.search_history),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensionResource(id = R.dimen.search_history_margin))
            )
//            LazyColumn(
//                modifier = Modifier.weight(1f)
//            ) {
//                items(history) { item ->
//                    Text(
//                        text = item,
//                        modifier = Modifier.padding(vertical = 8.dp)
//                    )
//                }
//            }
            Button(
                onClick = onClearHistoryClick,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = dimensionResource(id = R.dimen.button_margin_bottom))
            ) {
                Text(text = stringResource(id = R.string.clear_history))
            }
        }
    }
}