package com.lucasoliveira.bliss.data.mapper

import com.lucasoliveira.bliss.data.local.entity.EmojiEntity
import com.lucasoliveira.bliss.domain.model.Emoji
import org.junit.Assert.assertEquals
import org.junit.Test

class EmojiMapperTest {

    @Test
    fun `given emojis map, when toEmojiEntities is called, then return one entity per entry`() {
        val emojis = mapOf(
            "+1" to "https://emoji/thumbs-up.png",
            "octocat" to "https://emoji/octocat.png"
        )

        val result = emojis.toEmojiEntities()

        assertEquals(
            listOf(
                EmojiEntity(name = "+1", url = "https://emoji/thumbs-up.png"),
                EmojiEntity(name = "octocat", url = "https://emoji/octocat.png")
            ),
            result
        )
    }

    @Test
    fun `given empty map, when toEmojiEntities is called, then return empty list`() {
        assertEquals(emptyList<EmojiEntity>(), emptyMap<String, String>().toEmojiEntities())
    }

    @Test
    fun `given emoji entity, when toDomain is called, then return emoji with same values`() {
        val entity = EmojiEntity(name = "+1", url = "https://emoji/thumbs-up.png")

        assertEquals(Emoji(name = "+1", url = "https://emoji/thumbs-up.png"), entity.toDomain())
    }
}
