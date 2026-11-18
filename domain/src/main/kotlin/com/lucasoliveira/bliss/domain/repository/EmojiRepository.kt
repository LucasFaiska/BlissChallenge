package com.lucasoliveira.bliss.domain.repository

import com.lucasoliveira.bliss.domain.model.Emoji

interface EmojiRepository {

    suspend fun getEmojis(): Result<List<Emoji>>
}
