package com.lucasoliveira.bliss.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow

@Composable
fun HomeSideEffectHandler(
    sideEffect: Flow<HomeSideEffect>,
    onNavigateToEmojiList: () -> Unit,
    onNavigateToAvatarList: () -> Unit,
    onNavigateToGoogleRepos: () -> Unit,
) {
    LaunchedEffect(sideEffect) {
        sideEffect.collect { effect ->
            when (effect) {
                HomeSideEffect.NavigateToEmojiList -> onNavigateToEmojiList()
                HomeSideEffect.NavigateToAvatarList -> onNavigateToAvatarList()
                HomeSideEffect.NavigateToGoogleRepos -> onNavigateToGoogleRepos()
            }
        }
    }
}
