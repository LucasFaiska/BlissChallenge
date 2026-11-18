package com.lucasoliveira.bliss.presentation.emojilist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasoliveira.bliss.domain.model.Emoji
import com.lucasoliveira.bliss.domain.usecase.GetEmojisUseCase
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
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmojiListState())
    val uiState: StateFlow<EmojiListState> = _uiState.asStateFlow()

    fun onIntent(intent: EmojiListIntent) {
        when (intent) {
            EmojiListIntent.LoadEmojis -> loadEmojis(isRefresh = false)
            EmojiListIntent.Refresh -> loadEmojis(isRefresh = true)
            is EmojiListIntent.EmojiClicked -> removeEmoji(intent.emoji)
        }
    }

    private fun loadEmojis(isRefresh: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = !isRefresh, isRefreshing = isRefresh, errorMessage = null)
            }
            getEmojisUseCase()
                .catch { error ->
                    _uiState.update {
                        it.copy(isLoading = false, isRefreshing = false, errorMessage = error.message)
                    }
                }
                .collect { emojis ->
                    _uiState.update { it.copy(emojis = emojis, isLoading = false, isRefreshing = false) }
                }
        }
    }

    private fun removeEmoji(emoji: Emoji) {
        _uiState.update { it.copy(emojis = it.emojis - emoji) }
    }
}
