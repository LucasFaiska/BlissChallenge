package com.lucasoliveira.bliss.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.lucasoliveira.bliss.data.local.entity.AvatarEntity
import com.lucasoliveira.bliss.data.local.entity.EmojiEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalDataSourceImplTest {

    private lateinit var database: BlissDatabase
    private lateinit var localDataSource: LocalDataSource

    private val thumbsUp = EmojiEntity(name = "+1", url = "https://emoji/thumbs-up.png")
    private val thumbsDown = EmojiEntity(name = "-1", url = "https://emoji/thumbs-down.png")
    private val blissAvatar = AvatarEntity(
        login = "blissapps",
        id = 223156L,
        avatarUrl = "https://avatars.githubusercontent.com/u/223156"
    )

    @Before
    fun createDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            BlissDatabase::class.java
        ).build()
        localDataSource = LocalDataSourceImpl(database)
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun given_emojis_when_insertEmojis_then_getEmojis_returns_them() = runTest {
        localDataSource.insertEmojis(listOf(thumbsDown, thumbsUp))

        assertEquals(listOf(thumbsUp, thumbsDown), localDataSource.getEmojis())
    }

    @Test
    fun given_no_emojis_when_getEmojis_then_returns_empty_list() = runTest {
        assertEquals(emptyList<EmojiEntity>(), localDataSource.getEmojis())
    }

    @Test
    fun given_emojis_when_insertEmojis_then_countEmojis_returns_total() = runTest {
        localDataSource.insertEmojis(listOf(thumbsUp, thumbsDown))

        assertEquals(2, localDataSource.countEmojis())
    }

    @Test
    fun given_no_emojis_when_countEmojis_then_returns_zero() = runTest {
        assertEquals(0, localDataSource.countEmojis())
    }

    @Test
    fun given_avatar_when_insertAvatar_then_getAvatarByLogin_returns_it() = runTest {
        localDataSource.insertAvatar(blissAvatar)

        assertEquals(blissAvatar, localDataSource.getAvatarByLogin("blissapps"))
    }

    @Test
    fun given_existing_avatar_when_insertAvatar_with_same_login_then_it_is_replaced() = runTest {
        localDataSource.insertAvatar(blissAvatar)
        val updatedAvatar = blissAvatar.copy(avatarUrl = "https://avatars.githubusercontent.com/u/223156?v=5")

        localDataSource.insertAvatar(updatedAvatar)

        assertEquals(updatedAvatar, localDataSource.getAvatarByLogin("blissapps"))
    }

    @Test
    fun given_unknown_login_when_getAvatarByLogin_then_returns_null() = runTest {
        assertNull(localDataSource.getAvatarByLogin("unknown"))
    }

    @Test
    fun given_avatar_when_deleteAvatarByLogin_then_it_is_removed() = runTest {
        localDataSource.insertAvatar(blissAvatar)

        localDataSource.deleteAvatarByLogin("blissapps")

        assertNull(localDataSource.getAvatarByLogin("blissapps"))
    }

    @Test
    fun given_observer_when_avatar_is_inserted_and_deleted_then_observeAvatars_emits_each_change() = runTest {
        localDataSource.observeAvatars().test {
            assertEquals(emptyList<AvatarEntity>(), awaitItem())

            localDataSource.insertAvatar(blissAvatar)
            assertEquals(listOf(blissAvatar), awaitItem())

            localDataSource.deleteAvatarByLogin("blissapps")
            assertEquals(emptyList<AvatarEntity>(), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}
