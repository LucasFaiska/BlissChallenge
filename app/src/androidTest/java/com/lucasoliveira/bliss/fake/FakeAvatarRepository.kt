package com.lucasoliveira.bliss.fake

import com.lucasoliveira.bliss.domain.model.Avatar
import com.lucasoliveira.bliss.domain.repository.AvatarRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeAvatarRepository @Inject constructor() : AvatarRepository {

    private val githubUsers = mapOf(
        "blissapps" to Avatar(login = "blissapps", id = 7953004L, avatarUrl = "https://avatars.githubusercontent.com/u/7953004?v=4"),
        "google" to Avatar(login = "google", id = 1342004L, avatarUrl = "https://avatars.githubusercontent.com/u/1342004?v=4"),
    )

    private val saved = linkedMapOf<String, Avatar>()

    override suspend fun getAvatar(login: String): Result<Avatar> {
        val avatar = githubUsers[login.lowercase()] ?: return Result.failure(Exception("User $login not found"))
        saved[avatar.login] = avatar
        return Result.success(avatar)
    }

    override suspend fun getAvatars(): Result<List<Avatar>> = Result.success(saved.values.toList())

    override suspend fun deleteAvatar(login: String): Result<Unit> {
        saved.remove(login)
        return Result.success(Unit)
    }
}
