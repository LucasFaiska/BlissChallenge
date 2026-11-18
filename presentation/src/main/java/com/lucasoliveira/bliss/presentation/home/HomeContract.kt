package com.lucasoliveira.bliss.presentation.home

data class HomeState(
    val imageUrl: String? = null,
    val query: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface HomeIntent {
    data object RandomEmojiClicked : HomeIntent
    data class QueryChanged(val query: String) : HomeIntent
    data object SearchClicked : HomeIntent
    data object EmojiListClicked : HomeIntent
    data object AvatarListClicked : HomeIntent
    data object GoogleReposClicked : HomeIntent
    data object ErrorDismissed : HomeIntent
}

sealed interface HomeSideEffect {
    data object NavigateToEmojiList : HomeSideEffect
    data object NavigateToAvatarList : HomeSideEffect
    data object NavigateToGoogleRepos : HomeSideEffect
}
