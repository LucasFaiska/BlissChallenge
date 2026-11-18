package com.lucasoliveira.bliss.presentation.avatarlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AvatarListRoot(
    viewModel: AvatarListViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.onIntent(AvatarListIntent.LoadAvatars)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AvatarListScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
    )
}
