package com.lucasoliveira.bliss.data.mapper

import com.lucasoliveira.bliss.data.local.entity.AvatarEntity
import com.lucasoliveira.bliss.data.remote.dto.UserResponseDto
import com.lucasoliveira.bliss.domain.model.Avatar
import org.junit.Assert.assertEquals
import org.junit.Test

class AvatarMapperTest {

    @Test
    fun `given user response, when toEntity is called, then return avatar entity with same values`() {
        val user = UserResponseDto(login = "blissapps", id = 223156L, avatarUrl = "https://avatars/223156")

        assertEquals(
            AvatarEntity(login = "blissapps", id = 223156L, avatarUrl = "https://avatars/223156"),
            user.toEntity()
        )
    }

    @Test
    fun `given avatar entity, when toDomain is called, then return avatar with same values`() {
        val entity = AvatarEntity(login = "blissapps", id = 223156L, avatarUrl = "https://avatars/223156")

        assertEquals(
            Avatar(login = "blissapps", id = 223156L, avatarUrl = "https://avatars/223156"),
            entity.toDomain()
        )
    }
}
