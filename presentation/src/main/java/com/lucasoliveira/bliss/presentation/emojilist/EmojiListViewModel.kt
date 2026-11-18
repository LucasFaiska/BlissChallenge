package com.lucasoliveira.bliss.presentation.emojilist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasoliveira.bliss.domain.model.Emoji
import com.lucasoliveira.bliss.domain.usecase.GetEmojisUseCase
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
class EmojiListViewModel @Inject constructor(
    private val getEmojisUseCase: GetEmojisUseCase,
    private val snackbarController: SnackbarController,
) : ViewModel() {

    private val _uiState = MutableStateFlow<EmojiListUiState>(EmojiListUiState.Loading)
    val uiState: StateFlow<EmojiListUiState> = _uiState.asStateFlow()

    fun onIntent(intent: EmojiListIntent) {
        when (intent) {
            EmojiListIntent.LoadEmojis -> loadEmojis()
            EmojiListIntent.Refresh -> refresh()
            is EmojiListIntent.EmojiClicked -> removeEmoji(intent.emoji)
        }
    }

    private fun loadEmojis() {
        _uiState.value = EmojiListUiState.Loading
        collectEmojis()
    }

    private fun refresh() {
        _uiState.update { state ->
            if (state is EmojiListUiState.Success) state.copy(isRefreshing = true) else EmojiListUiState.Loading
        }
        collectEmojis()
    }

    private fun collectEmojis() {
        viewModelScope.launch {
            getEmojisUseCase()
                .catch { error -> handleError(error) }
                .collect { emojis -> _uiState.value = EmojiListUiState.Success(emojis) }
        }
    }

    private suspend fun handleError(error: Throwable) {
        val state = _uiState.value
        if (state is EmojiListUiState.Success) {
            _uiState.value = state.copy(isRefreshing = false)
            snackbarController.show(SnackbarMessage(error.message))
        } else {
            _uiState.value = EmojiListUiState.Error(error.message)
        }
    }

    private fun removeEmoji(emoji: Emoji) {
        _uiState.update { state ->
            if (state is EmojiListUiState.Success) state.copy(emojis = state.emojis - emoji) else state
        }
    }
}
