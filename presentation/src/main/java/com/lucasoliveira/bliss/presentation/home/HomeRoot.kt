package com.lucasoliveira.bliss.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeRoot(
    onNavigateToEmojiList: () -> Unit,
    onNavigateToAvatarList: () -> Unit,
    onNavigateToGoogleRepos: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    HomeSideEffectHandler(
        sideEffect = viewModel.sideEffect,
        onNavigateToEmojiList = onNavigateToEmojiList,
        onNavigateToAvatarList = onNavigateToAvatarList,
        onNavigateToGoogleRepos = onNavigateToGoogleRepos,
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
    )
}
