package com.lucasoliveira.bliss.fake

import com.lucasoliveira.bliss.domain.model.Emoji
import com.lucasoliveira.bliss.domain.repository.EmojiRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeEmojiRepository @Inject constructor() : EmojiRepository {

    val emojis = listOf(
        Emoji(name = "+1", url = "https://github.githubassets.com/images/icons/emoji/unicode/1f44d.png"),
        Emoji(name = "rocket", url = "https://github.githubassets.com/images/icons/emoji/unicode/1f680.png"),
        Emoji(name = "tada", url = "https://github.githubassets.com/images/icons/emoji/unicode/1f389.png"),
    )

    override suspend fun getEmojis(): Result<List<Emoji>> = Result.success(emojis)
}
