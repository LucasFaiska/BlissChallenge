package com.lucasoliveira.bliss.presentation.avatarlist

import com.lucasoliveira.bliss.domain.model.Avatar

data class AvatarListState(
    val avatars: List<Avatar> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface AvatarListIntent {
    data object LoadAvatars : AvatarListIntent
    data class AvatarClicked(val avatar: Avatar) : AvatarListIntent
    data object ErrorDismissed : AvatarListIntent
}
