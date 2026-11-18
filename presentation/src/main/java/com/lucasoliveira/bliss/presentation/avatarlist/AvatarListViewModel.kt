package com.lucasoliveira.bliss.presentation.avatarlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasoliveira.bliss.domain.model.Avatar
import com.lucasoliveira.bliss.domain.usecase.DeleteAvatarUseCase
import com.lucasoliveira.bliss.domain.usecase.GetAvatarsUseCase
import com.lucasoliveira.bliss.presentation.snackbar.SnackbarController
import com.lucasoliveira.bliss.presentation.snackbar.SnackbarMessage
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
    private val snackbarController: SnackbarController,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AvatarListUiState>(AvatarListUiState.Loading)
    val uiState: StateFlow<AvatarListUiState> = _uiState.asStateFlow()

    fun onIntent(intent: AvatarListIntent) {
        when (intent) {
            AvatarListIntent.LoadAvatars -> loadAvatars()
            is AvatarListIntent.AvatarClicked -> deleteAvatar(intent.avatar)
        }
    }

    private fun loadAvatars() {
        viewModelScope.launch {
            _uiState.value = AvatarListUiState.Loading
            getAvatarsUseCase()
                .catch { error -> _uiState.value = AvatarListUiState.Error(error.message) }
                .collect { avatars -> _uiState.value = AvatarListUiState.Success(avatars) }
        }
    }

    private fun deleteAvatar(avatar: Avatar) {
        viewModelScope.launch {
            deleteAvatarUseCase(avatar.login)
                .catch { error -> snackbarController.show(SnackbarMessage(error.message)) }
                .collect {
                    _uiState.update { state ->
                        if (state is AvatarListUiState.Success) state.copy(avatars = state.avatars - avatar) else state
                    }
                }
        }
    }
}
