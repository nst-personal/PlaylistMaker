package com.example.playlistmaker.presentation.ui.search.functions

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.asFlow
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.common.TrackItem
import com.example.playlistmaker.presentation.ui.search.SearchViewModel
import com.example.playlistmaker.presentation.ui.search.interfaces.TrackScreenState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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
    val scope = rememberCoroutineScope()
    var debounceJob by remember { mutableStateOf<Job?>(null) }
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

    LaunchedEffect(query) {
        debounceJob?.cancel()
        if (query.isNotEmpty()) {
            searchProgressBar = true
        }
        debounceJob = scope.launch {
            delay(300L)
            viewModel.searchDebounce(
                changedText = query
            )
        }
    }

    Scaffold(
        topBar = { AppToolbar() },
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
fun AppToolbar() {
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
fun NotFound() {
    val textColor = if (isSystemInDarkTheme()) {
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
            painter = painterResource(id = R.drawable.not_found),
            contentDescription = null,
            modifier = Modifier.size(dimensionResource(id = R.dimen.icon_issue))
        )
        Text(
            text = stringResource(id = R.string.search_not_found),
            fontWeight = FontWeight(400),
            fontSize = 19.sp,
            color = textColor,
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
    val titleColor = if (isSystemInDarkTheme()) {
        colorResource(id = R.color.white)
    } else {
        colorResource(id = R.color.black)
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
            color = titleColor,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(id = R.string.search_error_check_connection),
            modifier = Modifier.padding(top = dimensionResource(id = R.dimen.sub_description)),
            fontWeight = FontWeight(400),
            fontSize = 19.sp,
            color = titleColor,
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
            textAlign = TextAlign.Center,
            color = backgroundColor
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