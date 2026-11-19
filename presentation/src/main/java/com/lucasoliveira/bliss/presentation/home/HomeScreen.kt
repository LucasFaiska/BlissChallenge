package com.lucasoliveira.bliss.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lucasoliveira.bliss.presentation.R
import com.lucasoliveira.bliss.presentation.component.BlissActionButton
import com.lucasoliveira.bliss.presentation.component.BlissImage
import com.lucasoliveira.bliss.presentation.component.BlissLoading
import com.lucasoliveira.bliss.presentation.component.BlissSearchField
import com.lucasoliveira.bliss.presentation.theme.BlissTheme
import com.lucasoliveira.bliss.presentation.theme.BulbYellow

private val ImageSize = 96.dp
private val ShortButtonWidth = 168.dp
private val LongButtonWidth = 216.dp
private val SearchWidth = 280.dp

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onIntent: (HomeIntent) -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    val isLoading = uiState is HomeUiState.Loading

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            HomeImage(uiState = uiState)
            Spacer(modifier = Modifier.height(24.dp))
            BlissActionButton(
                text = stringResource(R.string.home_random_emoji),
                onClick = { onIntent(HomeIntent.RandomEmojiClicked) },
                enabled = !isLoading,
                modifier = Modifier.width(ShortButtonWidth),
            )
            BlissActionButton(
                text = stringResource(R.string.home_emoji_list),
                onClick = { onIntent(HomeIntent.EmojiListClicked) },
                modifier = Modifier.width(ShortButtonWidth),
            )
            Spacer(modifier = Modifier.height(48.dp))
            BlissSearchField(
                query = query,
                onQueryChange = { query = it },
                onSearch = { onIntent(HomeIntent.SearchClicked(query)) },
                placeholder = stringResource(R.string.home_search_placeholder),
                searchContentDescription = stringResource(R.string.home_search),
                enabled = !isLoading,
                modifier = Modifier.width(SearchWidth),
            )
            BlissActionButton(
                text = stringResource(R.string.home_avatar_list),
                onClick = { onIntent(HomeIntent.AvatarListClicked) },
                modifier = Modifier.width(LongButtonWidth),
            )
            BlissActionButton(
                text = stringResource(R.string.home_google_repos),
                onClick = { onIntent(HomeIntent.GoogleReposClicked) },
                modifier = Modifier.width(LongButtonWidth),
            )
        }
    }
}

@Composable
private fun HomeImage(uiState: HomeUiState) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(ImageSize)) {
        when (uiState) {
            HomeUiState.Loading -> BlissLoading()
            is HomeUiState.Success -> {
                var isImageLoading by remember(uiState.imageUrl) { mutableStateOf(true) }
                BlissImage(
                    url = uiState.imageUrl,
                    contentDescription = stringResource(R.string.home_image_description),
                    onLoadingChange = { isImageLoading = it },
                    modifier = Modifier.size(ImageSize),
                )
                if (isImageLoading && !LocalInspectionMode.current) {
                    BlissLoading()
                }
            }
            HomeUiState.Idle -> Icon(
                imageVector = Icons.Filled.Lightbulb,
                contentDescription = stringResource(R.string.home_default_image_description),
                tint = BulbYellow,
                modifier = Modifier.size(ImageSize),
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HomeScreenIdlePreview() {
    BlissTheme {
        HomeScreen(uiState = HomeUiState.Idle, onIntent = {})
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HomeScreenLoadingPreview() {
    BlissTheme {
        HomeScreen(uiState = HomeUiState.Loading, onIntent = {})
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HomeScreenSuccessPreview() {
    BlissTheme {
        HomeScreen(
            uiState = HomeUiState.Success(imageUrl = "https://avatars.githubusercontent.com/u/223156?v=4"),
            onIntent = {},
        )
    }
}
