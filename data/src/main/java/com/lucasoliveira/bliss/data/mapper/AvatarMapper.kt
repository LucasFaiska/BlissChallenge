package com.lucasoliveira.bliss.data.mapper

import com.lucasoliveira.bliss.data.local.entity.AvatarEntity
import com.lucasoliveira.bliss.data.remote.dto.UserResponseDto
import com.lucasoliveira.bliss.domain.model.Avatar

internal fun UserResponseDto.toEntity(): AvatarEntity =
    AvatarEntity(login = login, id = id, avatarUrl = avatarUrl)

internal fun AvatarEntity.toDomain(): Avatar =
    Avatar(login = login, id = id, avatarUrl = avatarUrl)
