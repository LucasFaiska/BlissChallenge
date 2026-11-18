package com.lucasoliveira.bliss.presentation.emojilist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.lucasoliveira.bliss.domain.model.Emoji
import com.lucasoliveira.bliss.presentation.R
import com.lucasoliveira.bliss.presentation.component.BlissErrorView
import com.lucasoliveira.bliss.presentation.component.BlissImageGrid
import com.lucasoliveira.bliss.presentation.component.BlissLoading
import com.lucasoliveira.bliss.presentation.theme.BlissTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmojiListScreen(
    uiState: EmojiListUiState,
    onIntent: (EmojiListIntent) -> Unit,
) {
    PullToRefreshBox(
        isRefreshing = (uiState as? EmojiListUiState.Success)?.isRefreshing == true,
        onRefresh = { onIntent(EmojiListIntent.Refresh) },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding(),
    ) {
        when (uiState) {
            EmojiListUiState.Loading -> BlissLoading(modifier = Modifier.fillMaxSize())
            is EmojiListUiState.Success -> BlissImageGrid(
                items = uiState.emojis,
                key = { it.name },
                imageUrl = { it.url },
                contentDescription = { it.name },
                onItemClick = { onIntent(EmojiListIntent.EmojiClicked(it)) },
                bordered = true,
            )
            is EmojiListUiState.Error -> BlissErrorView(
                title = stringResource(R.string.error_title),
                message = uiState.message ?: stringResource(R.string.error_generic),
                retryLabel = stringResource(R.string.error_retry),
                onRetry = { onIntent(EmojiListIntent.LoadEmojis) },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

private val previewEmojis = listOf(
    "+1", "-1", "100", "1234", "1st_place_medal", "2nd_place_medal", "3rd_place_medal", "8ball",
    "a", "ab", "abc", "abcd", "accept", "aerial_tramway", "afghanistan", "airplane",
).map { Emoji(name = it, url = "https://github.githubassets.com/images/icons/emoji/$it.png") }

@Preview(showSystemUi = true)
@Composable
private fun EmojiListScreenLoadingPreview() {
    BlissTheme {
        EmojiListScreen(uiState = EmojiListUiState.Loading, onIntent = {})
    }
}

@Preview(showSystemUi = true)
@Composable
private fun EmojiListScreenSuccessPreview() {
    BlissTheme {
        EmojiListScreen(uiState = EmojiListUiState.Success(emojis = previewEmojis), onIntent = {})
    }
}

@Preview(showSystemUi = true)
@Composable
private fun EmojiListScreenRefreshingPreview() {
    BlissTheme {
        EmojiListScreen(
            uiState = EmojiListUiState.Success(emojis = previewEmojis, isRefreshing = true),
            onIntent = {},
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun EmojiListScreenErrorPreview() {
    BlissTheme {
        EmojiListScreen(
            uiState = EmojiListUiState.Error(message = "API rate limit reached. Try again at 14:32"),
            onIntent = {},
        )
    }
}
