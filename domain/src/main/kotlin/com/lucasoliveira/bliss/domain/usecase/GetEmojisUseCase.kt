package com.lucasoliveira.bliss.domain.usecase

import com.lucasoliveira.bliss.domain.error.DomainException
import com.lucasoliveira.bliss.domain.model.Emoji
import com.lucasoliveira.bliss.domain.repository.EmojiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetEmojisUseCase @Inject constructor(
    private val emojiRepository: EmojiRepository
) {

    operator fun invoke(): Flow<List<Emoji>> = flow {
        emit(
            emojiRepository.getEmojis().getOrElse { error ->
                throw DomainException(error.message, error)
            }
        )
    }
}
