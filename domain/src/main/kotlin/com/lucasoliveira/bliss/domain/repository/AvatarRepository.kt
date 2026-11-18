package com.lucasoliveira.bliss.domain.repository

import com.lucasoliveira.bliss.domain.model.Avatar

interface AvatarRepository {

    suspend fun getAvatar(login: String): Result<Avatar>

    suspend fun getAvatars(): Result<List<Avatar>>

    suspend fun deleteAvatar(login: String): Result<Unit>
}
