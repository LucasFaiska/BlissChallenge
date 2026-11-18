package com.lucasoliveira.bliss.presentation.home

sealed interface HomeUiState {
    data object Idle : HomeUiState
    data object Loading : HomeUiState
    data class Success(val imageUrl: String) : HomeUiState
}

sealed interface HomeIntent {
    data object RandomEmojiClicked : HomeIntent
    data class SearchClicked(val query: String) : HomeIntent
    data object EmojiListClicked : HomeIntent
    data object AvatarListClicked : HomeIntent
    data object GoogleReposClicked : HomeIntent
}

sealed interface HomeSideEffect {
    data object NavigateToEmojiList : HomeSideEffect
    data object NavigateToAvatarList : HomeSideEffect
    data object NavigateToGoogleRepos : HomeSideEffect
}
