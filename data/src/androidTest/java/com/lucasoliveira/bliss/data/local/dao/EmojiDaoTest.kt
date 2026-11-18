package com.lucasoliveira.bliss.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lucasoliveira.bliss.data.local.BlissDatabase
import com.lucasoliveira.bliss.data.local.entity.EmojiEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EmojiDaoTest {

    private lateinit var database: BlissDatabase
    private lateinit var emojiDao: EmojiDao

    @Before
    fun createDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            BlissDatabase::class.java
        ).build()
        emojiDao = database.emojiDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun given_emojis_when_insertAll_then_getAll_returns_them_ordered_by_name() = runTest {
        val thumbsDown = EmojiEntity(name = "-1", url = "https://emoji/thumbs-down.png")
        val thumbsUp = EmojiEntity(name = "+1", url = "https://emoji/thumbs-up.png")
        val hundred = EmojiEntity(name = "100", url = "https://emoji/100.png")

        emojiDao.insertAll(listOf(hundred, thumbsDown, thumbsUp))

        assertEquals(listOf(thumbsUp, thumbsDown, hundred), emojiDao.getAll())
    }

    @Test
    fun given_existing_emoji_when_insertAll_with_same_name_then_url_is_replaced() = runTest {
        emojiDao.insertAll(listOf(EmojiEntity(name = "+1", url = "https://emoji/old.png")))

        emojiDao.insertAll(listOf(EmojiEntity(name = "+1", url = "https://emoji/new.png")))

        assertEquals(listOf(EmojiEntity(name = "+1", url = "https://emoji/new.png")), emojiDao.getAll())
    }

    @Test
    fun given_emojis_when_count_then_returns_total() = runTest {
        emojiDao.insertAll(
            listOf(
                EmojiEntity(name = "+1", url = "https://emoji/thumbs-up.png"),
                EmojiEntity(name = "-1", url = "https://emoji/thumbs-down.png")
            )
        )

        assertEquals(2, emojiDao.count())
    }

    @Test
    fun given_empty_table_when_count_then_returns_zero() = runTest {
        assertEquals(0, emojiDao.count())
    }
}
