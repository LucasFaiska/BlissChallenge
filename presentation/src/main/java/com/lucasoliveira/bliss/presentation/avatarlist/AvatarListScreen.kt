package com.lucasoliveira.bliss.presentation.avatarlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.lucasoliveira.bliss.domain.model.Avatar
import com.lucasoliveira.bliss.presentation.R
import com.lucasoliveira.bliss.presentation.component.BlissEmptyMessage
import com.lucasoliveira.bliss.presentation.component.BlissErrorView
import com.lucasoliveira.bliss.presentation.component.BlissImageGrid
import com.lucasoliveira.bliss.presentation.component.BlissLoading
import com.lucasoliveira.bliss.presentation.theme.BlissTheme

@Composable
fun AvatarListScreen(
    uiState: AvatarListUiState,
    onIntent: (AvatarListIntent) -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding(),
    ) {
        when (uiState) {
            AvatarListUiState.Loading -> BlissLoading(modifier = Modifier.fillMaxSize())
            is AvatarListUiState.Success -> if (uiState.avatars.isEmpty()) {
                BlissEmptyMessage(message = stringResource(R.string.avatar_list_empty))
            } else {
                BlissImageGrid(
                    items = uiState.avatars,
                    key = { it.login },
                    imageUrl = { it.avatarUrl },
                    contentDescription = { it.login },
                    onItemClick = { onIntent(AvatarListIntent.AvatarClicked(it)) },
                )
            }
            is AvatarListUiState.Error -> BlissErrorView(
                title = stringResource(R.string.error_title),
                message = uiState.message ?: stringResource(R.string.error_generic),
                retryLabel = stringResource(R.string.error_retry),
                onRetry = { onIntent(AvatarListIntent.LoadAvatars) },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

private val previewAvatars = listOf("blissapps", "jetbrains", "google", "reactivex", "square", "android")
    .mapIndexed { index, login -> Avatar(login = login, id = index.toLong(), avatarUrl = "https://avatars/$login") }

@Preview(showSystemUi = true)
@Composable
private fun AvatarListScreenLoadingPreview() {
    BlissTheme {
        AvatarListScreen(uiState = AvatarListUiState.Loading, onIntent = {})
    }
}

@Preview(showSystemUi = true)
@Composable
private fun AvatarListScreenSuccessPreview() {
    BlissTheme {
        AvatarListScreen(uiState = AvatarListUiState.Success(avatars = previewAvatars), onIntent = {})
    }
}

@Preview(showSystemUi = true)
@Composable
private fun AvatarListScreenEmptyPreview() {
    BlissTheme {
        AvatarListScreen(uiState = AvatarListUiState.Success(avatars = emptyList()), onIntent = {})
    }
}

@Preview(showSystemUi = true)
@Composable
private fun AvatarListScreenErrorPreview() {
    BlissTheme {
        AvatarListScreen(uiState = AvatarListUiState.Error(message = "Could not delete avatar"), onIntent = {})
    }
}
