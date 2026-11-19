package com.lucasoliveira.bliss.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasoliveira.bliss.domain.usecase.GetAvatarUseCase
import com.lucasoliveira.bliss.domain.usecase.GetEmojisUseCase
import com.lucasoliveira.bliss.presentation.snackbar.SnackbarController
import com.lucasoliveira.bliss.presentation.snackbar.SnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getEmojisUseCase: GetEmojisUseCase,
    private val getAvatarUseCase: GetAvatarUseCase,
    private val snackbarController: SnackbarController,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _sideEffect = Channel<HomeSideEffect>(Channel.BUFFERED)
    val sideEffect: Flow<HomeSideEffect> = _sideEffect.receiveAsFlow()

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.RandomEmojiClicked -> showRandomEmoji()
            is HomeIntent.SearchClicked -> searchAvatar(intent.query)
            HomeIntent.EmojiListClicked -> sendSideEffect(HomeSideEffect.NavigateToEmojiList)
            HomeIntent.AvatarListClicked -> sendSideEffect(HomeSideEffect.NavigateToAvatarList)
            HomeIntent.GoogleReposClicked -> sendSideEffect(HomeSideEffect.NavigateToGoogleRepos)
        }
    }

    private fun showRandomEmoji() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            getEmojisUseCase()
                .catch { error -> showError(error) }
                .collect { emojis ->
                    _uiState.value = emojis.randomOrNull()
                        ?.let { HomeUiState.Success(it.url) }
                        ?: HomeUiState.Idle
                }
        }
    }

    private fun searchAvatar(query: String) {
        val login = query.trim()
        if (login.isEmpty()) return

        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            getAvatarUseCase(login)
                .catch { error -> showError(error) }
                .collect { avatar -> _uiState.value = HomeUiState.Success(avatar.avatarUrl) }
        }
    }

    private suspend fun showError(error: Throwable) {
        _uiState.value = HomeUiState.Idle
        snackbarController.show(SnackbarMessage(error.message))
    }

    private fun sendSideEffect(sideEffect: HomeSideEffect) {
        viewModelScope.launch { _sideEffect.send(sideEffect) }
    }
}
