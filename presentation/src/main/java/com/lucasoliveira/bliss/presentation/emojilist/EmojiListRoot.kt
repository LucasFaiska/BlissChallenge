package com.lucasoliveira.bliss.presentation.emojilist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun EmojiListRoot(
    viewModel: EmojiListViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.onIntent(EmojiListIntent.LoadEmojis)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EmojiListScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
    )
}
