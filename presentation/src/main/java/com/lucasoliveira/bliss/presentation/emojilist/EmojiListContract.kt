package com.lucasoliveira.bliss.presentation.emojilist

import com.lucasoliveira.bliss.domain.model.Emoji

data class EmojiListState(
    val emojis: List<Emoji> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface EmojiListIntent {
    data object LoadEmojis : EmojiListIntent
    data object Refresh : EmojiListIntent
    data class EmojiClicked(val emoji: Emoji) : EmojiListIntent
}
