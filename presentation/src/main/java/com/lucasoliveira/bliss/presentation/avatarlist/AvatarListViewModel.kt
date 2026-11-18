package com.lucasoliveira.bliss.presentation.avatarlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasoliveira.bliss.domain.model.Avatar
import com.lucasoliveira.bliss.domain.usecase.DeleteAvatarUseCase
import com.lucasoliveira.bliss.domain.usecase.GetAvatarsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AvatarListViewModel @Inject constructor(
    private val getAvatarsUseCase: GetAvatarsUseCase,
    private val deleteAvatarUseCase: DeleteAvatarUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AvatarListState())
    val uiState: StateFlow<AvatarListState> = _uiState.asStateFlow()

    fun onIntent(intent: AvatarListIntent) {
        when (intent) {
            AvatarListIntent.LoadAvatars -> loadAvatars()
            is AvatarListIntent.AvatarClicked -> deleteAvatar(intent.avatar)
            AvatarListIntent.ErrorDismissed -> _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun loadAvatars() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getAvatarsUseCase()
                .catch { error -> _uiState.update { it.copy(isLoading = false, errorMessage = error.message) } }
                .collect { avatars -> _uiState.update { it.copy(avatars = avatars, isLoading = false) } }
        }
    }

    private fun deleteAvatar(avatar: Avatar) {
        viewModelScope.launch {
            deleteAvatarUseCase(avatar.login)
                .catch { error -> _uiState.update { it.copy(errorMessage = error.message) } }
                .collect { _uiState.update { it.copy(avatars = it.avatars - avatar) } }
        }
    }
}
