package com.lucasoliveira.bliss.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lucasoliveira.bliss.data.local.BlissDatabase
import com.lucasoliveira.bliss.data.local.entity.AvatarEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AvatarDaoTest {

    private lateinit var database: BlissDatabase
    private lateinit var avatarDao: AvatarDao

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
        avatarDao = database.avatarDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun given_avatar_when_insert_then_getByLogin_returns_it() = runTest {
        avatarDao.insert(blissAvatar)

        assertEquals(blissAvatar, avatarDao.getByLogin("blissapps"))
    }

    @Test
    fun given_avatar_when_getByLogin_with_different_case_then_returns_it() = runTest {
        avatarDao.insert(blissAvatar)

        assertEquals(blissAvatar, avatarDao.getByLogin("BlissApps"))
    }

    @Test
    fun given_unknown_login_when_getByLogin_then_returns_null() = runTest {
        assertNull(avatarDao.getByLogin("unknown"))
    }

    @Test
    fun given_avatar_when_deleteByLogin_then_it_is_removed() = runTest {
        avatarDao.insert(blissAvatar)

        avatarDao.deleteByLogin("blissapps")

        assertNull(avatarDao.getByLogin("blissapps"))
    }

    @Test
    fun given_avatars_when_getAll_then_returns_them_ordered_by_login() = runTest {
        val googleAvatar = AvatarEntity(
            login = "google",
            id = 1342004L,
            avatarUrl = "https://avatars.githubusercontent.com/u/1342004"
        )
        avatarDao.insert(googleAvatar)
        avatarDao.insert(blissAvatar)

        assertEquals(listOf(blissAvatar, googleAvatar), avatarDao.getAll())
    }

    @Test
    fun given_no_avatars_when_getAll_then_returns_empty_list() = runTest {
        assertEquals(emptyList<AvatarEntity>(), avatarDao.getAll())
    }
}
