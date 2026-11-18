package com.lucasoliveira.bliss.presentation.emojilist

import com.lucasoliveira.bliss.domain.model.Emoji

sealed interface EmojiListUiState {
    data object Loading : EmojiListUiState
    data class Success(
        val emojis: List<Emoji>,
        val isRefreshing: Boolean = false,
    ) : EmojiListUiState
    data class Error(val message: String?) : EmojiListUiState
}

sealed interface EmojiListIntent {
    data object LoadEmojis : EmojiListIntent
    data object Refresh : EmojiListIntent
    data class EmojiClicked(val emoji: Emoji) : EmojiListIntent
}
