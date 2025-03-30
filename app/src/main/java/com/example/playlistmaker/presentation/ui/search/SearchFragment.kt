package com.example.playlistmaker.presentation.ui.search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.asFlow
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.media_player.MediaPlayerActivity
import com.example.playlistmaker.presentation.ui.search.interfaces.TrackScreenState
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

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

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SearchScreen(
    viewModel: SearchViewModel, openMediaPlayer: (
        track: Track
    ) -> Unit,
    onClearHistoryClick: (

    ) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    var savedQuery by remember { mutableStateOf("") }
    var searchProgressBar by remember { mutableStateOf(false) }
    var tracks by remember { mutableStateOf<List<Track>?>(null) }
    var historyTracks by remember { mutableStateOf<List<Track>?>(listOf()) }

    val screenState by viewModel.getLoadingTrackLiveData().asFlow().collectAsState(initial = null)

    when (screenState) {
        is TrackScreenState.SearchContent -> {
            val state = screenState as TrackScreenState.SearchContent
            savedQuery = state.search
            tracks = state.tracks
            searchProgressBar = false
        }

        is TrackScreenState.HistoryContent -> {
            historyTracks = (screenState as TrackScreenState.HistoryContent).tracks
        }

        null -> {

        }
    }

    fun onFocus(hasFocus: Boolean) {
        if (hasFocus) {
            viewModel.showHistory()
        }
    }

    fun onTextChanged(queryValue: String) {
        query = queryValue
        if (queryValue.isNotEmpty()) {
            searchProgressBar = true
            viewModel.searchDebounce(
                changedText = query
            )
        } else {
            searchProgressBar = false
        }
    }

    fun clearText() {
        query = ""
        savedQuery = ""
        viewModel.showHistory()
        searchProgressBar = false
    }

    fun onRetryClick() {
        onTextChanged(savedQuery)
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
        )

        if (searchProgressBar) {
            Spacer(modifier = Modifier.height(16.dp))
            SearchProgressBar(isVisible = true)
        }

        if (!searchProgressBar) {
            if (query.isNotEmpty()) {
                if (tracks?.isNotEmpty() == true) {
                    Spacer(modifier = Modifier.height(16.dp))
                    TracksList(tracks = tracks!!, { track ->
                        viewModel.addTrack(track)
                        openMediaPlayer(track)
                    })
                }

                if (tracks?.isEmpty() == true) {
                    Spacer(modifier = Modifier.height(16.dp))
                    NotFound()
                }
                if (tracks == null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    ErrorOccured(onRetryClick = {
                        onRetryClick()
                    })
                }
            } else {
                if (historyTracks?.isNotEmpty() == true) {
                    Spacer(modifier = Modifier.height(16.dp))
                    HistoryContent(
                        historyTracks = historyTracks!!,
                        openMediaPlayer = openMediaPlayer,
                        onClearHistoryClick = onClearHistoryClick
                    )
                }
            }

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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(id = R.dimen.main_top_padding),
                    vertical = dimensionResource(id = R.dimen.main_top_padding),
                )
                .background(color = Color.Transparent)
        ) {
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
                .onFocusChanged { isFocus -> onFocus(isFocus.isFocused) }
                .background(colorResource(id = R.color.textbox), RoundedCornerShape(16.dp)),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.search),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = {
                        onClearClick()
                    },
                        modifier = Modifier
                            .clickable {
                                onClearClick()
                            }) {
                        Icon(
                            painter = painterResource(id = R.drawable.clear),
                            contentDescription = null,
                            modifier = Modifier
                                .clickable {
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
fun HistoryTracksList(
    tracks: List<Track>, openMediaPlayer: (
        track: Track
    ) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .wrapContentHeight(align = Alignment.Top)
            .padding(bottom = dimensionResource(id = R.dimen.list_bottom))
    ) {
        items(tracks) { track ->
            TrackItem(track = track, openMediaPlayer)
        }
    }
}


@Composable
fun TracksList(
    tracks: List<Track>, openMediaPlayer: (
        track: Track
    ) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = dimensionResource(id = R.dimen.view_padding),
                vertical = dimensionResource(id = R.dimen.scroll_search_container_margin)
            )
            .padding(bottom = dimensionResource(id = R.dimen.list_bottom)),
    ) {
        items(tracks) { track ->
            TrackItem(track = track, openMediaPlayer)
        }
    }
}

@Composable
fun TrackItem(
    track: Track,
    openMediaPlayer: (
        track: Track
    ) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = dimensionResource(id = R.dimen.item_top_padding),
                bottom = dimensionResource(id = R.dimen.item_top_padding)
            )
            .clickable {
                openMediaPlayer(track)
            },
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
            modifier = Modifier
                .width(24.dp)
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

    Column(
        modifier = Modifier.fillMaxWidth(0.85f)
    ) {
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
                modifier = Modifier
                    .widthIn(max = LocalConfiguration.current.screenWidthDp.dp * 0.45f)
                    .wrapContentWidth(align = Alignment.Start)
            )

            Spacer(modifier = Modifier.width(2.dp))

            Image(
                painter = painterResource(id = R.drawable.point),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(2.dp))

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
fun NotFound() {
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
            fontWeight = FontWeight(400),
            fontSize = 19.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = dimensionResource(id = R.dimen.margin_top))
        )
    }
}

@Composable
fun ErrorOccured(onRetryClick: () -> Unit) {
    val textColor = if (isSystemInDarkTheme()) {
        colorResource(id = R.color.black)
    } else {
        colorResource(id = R.color.white)
    }
    val backgroundColor = if (isSystemInDarkTheme()) {
        colorResource(id = R.color.white)
    } else {
        colorResource(id = R.color.black)
    }

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
            fontWeight = FontWeight(400),
            fontSize = 19.sp,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(id = R.string.search_error_check_connection),
            modifier = Modifier.padding(top = dimensionResource(id = R.dimen.sub_description)),
            fontWeight = FontWeight(400),
            fontSize = 19.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetryClick,
            shape = RoundedCornerShape(size = 24.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
            modifier = Modifier
                .padding(
                    start = dimensionResource(id = R.dimen.button_margin),
                    end = dimensionResource(id = R.dimen.button_margin),
                    top = dimensionResource(id = R.dimen.button_margin_bottom)
                )
        ) {
            Text(text = stringResource(id = R.string.search_retry), color = textColor)
        }
    }
}

@Composable
fun HistoryContent(
    historyTracks: List<Track>,
    openMediaPlayer: (
        track: Track
    ) -> Unit,
    onClearHistoryClick: () -> Unit
) {
    val textColor = if (isSystemInDarkTheme()) {
        colorResource(id = R.color.black)
    } else {
        colorResource(id = R.color.white)
    }
    val backgroundColor = if (isSystemInDarkTheme()) {
        colorResource(id = R.color.white)
    } else {
        colorResource(id = R.color.black)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = dimensionResource(id = R.dimen.view_padding),
                vertical = dimensionResource(id = R.dimen.scroll_search_container_margin)
            )
            .padding(bottom = dimensionResource(id = R.dimen.list_bottom))
    ) {
        Text(
            text = stringResource(id = R.string.search_history),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = dimensionResource(id = R.dimen.margin_search_title_margin),
                    bottom = dimensionResource(id = R.dimen.media_bottom_margin)
                ),
            fontWeight = FontWeight(500),
            fontSize = 19.sp,
            textAlign = TextAlign.Center
        )
        Row {
            HistoryTracksList(historyTracks, openMediaPlayer)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onClearHistoryClick,
                shape = RoundedCornerShape(size = 24.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
                modifier = Modifier
                    .padding(
                        start = dimensionResource(id = R.dimen.button_margin),
                        end = dimensionResource(id = R.dimen.button_margin),
                        top = dimensionResource(id = R.dimen.button_margin_bottom)
                    )
            ) {
                Text(text = stringResource(id = R.string.clear_history), color = textColor)
            }
        }
    }
}