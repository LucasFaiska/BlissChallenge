package com.lucasoliveira.bliss.presentation.avatarlist

import com.lucasoliveira.bliss.domain.model.Avatar

sealed interface AvatarListUiState {
    data object Loading : AvatarListUiState
    data class Success(val avatars: List<Avatar>) : AvatarListUiState
    data class Error(val message: String?) : AvatarListUiState
}

sealed interface AvatarListIntent {
    data object LoadAvatars : AvatarListIntent
    data class AvatarClicked(val avatar: Avatar) : AvatarListIntent
}
