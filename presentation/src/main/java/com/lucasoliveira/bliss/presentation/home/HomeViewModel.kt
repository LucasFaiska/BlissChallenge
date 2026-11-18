package com.lucasoliveira.bliss.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasoliveira.bliss.domain.usecase.GetAvatarUseCase
import com.lucasoliveira.bliss.domain.usecase.GetEmojisUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getEmojisUseCase: GetEmojisUseCase,
    private val getAvatarUseCase: GetAvatarUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    private val _sideEffect = Channel<HomeSideEffect>(Channel.BUFFERED)
    val sideEffect: Flow<HomeSideEffect> = _sideEffect.receiveAsFlow()

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.RandomEmojiClicked -> showRandomEmoji()
            is HomeIntent.QueryChanged -> _uiState.update { it.copy(query = intent.query) }
            HomeIntent.SearchClicked -> searchAvatar()
            HomeIntent.EmojiListClicked -> sendSideEffect(HomeSideEffect.NavigateToEmojiList)
            HomeIntent.AvatarListClicked -> sendSideEffect(HomeSideEffect.NavigateToAvatarList)
            HomeIntent.GoogleReposClicked -> sendSideEffect(HomeSideEffect.NavigateToGoogleRepos)
            HomeIntent.ErrorDismissed -> _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun showRandomEmoji() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getEmojisUseCase()
                .catch { error -> showError(error) }
                .collect { emojis ->
                    _uiState.update { it.copy(isLoading = false, imageUrl = emojis.randomOrNull()?.url) }
                }
        }
    }

    private fun searchAvatar() {
        val login = _uiState.value.query.trim()
        if (login.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getAvatarUseCase(login)
                .catch { error -> showError(error) }
                .collect { avatar ->
                    _uiState.update { it.copy(isLoading = false, imageUrl = avatar.avatarUrl) }
                }
        }
    }

    private fun showError(error: Throwable) {
        _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
    }

    private fun sendSideEffect(sideEffect: HomeSideEffect) {
        viewModelScope.launch { _sideEffect.send(sideEffect) }
    }
}
