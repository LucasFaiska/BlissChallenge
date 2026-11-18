package com.lucasoliveira.bliss.data.local

import com.lucasoliveira.bliss.data.local.entity.AvatarEntity
import com.lucasoliveira.bliss.data.local.entity.EmojiEntity
import javax.inject.Inject

internal interface LocalDataSource {

    suspend fun insertEmojis(emojis: List<EmojiEntity>)

    suspend fun getEmojis(): List<EmojiEntity>

    suspend fun countEmojis(): Int

    suspend fun insertAvatar(avatar: AvatarEntity)

    suspend fun getAvatarByLogin(login: String): AvatarEntity?

    suspend fun getAvatars(): List<AvatarEntity>

    suspend fun deleteAvatarByLogin(login: String)
}

internal class LocalDataSourceImpl @Inject constructor(
    private val database: BlissDatabase
) : LocalDataSource {

    override suspend fun insertEmojis(emojis: List<EmojiEntity>) =
        database.emojiDao().insertAll(emojis)

    override suspend fun getEmojis(): List<EmojiEntity> =
        database.emojiDao().getAll()

    override suspend fun countEmojis(): Int =
        database.emojiDao().count()

    override suspend fun insertAvatar(avatar: AvatarEntity) =
        database.avatarDao().insert(avatar)

    override suspend fun getAvatarByLogin(login: String): AvatarEntity? =
        database.avatarDao().getByLogin(login)

    override suspend fun getAvatars(): List<AvatarEntity> =
        database.avatarDao().getAll()

    override suspend fun deleteAvatarByLogin(login: String) =
        database.avatarDao().deleteByLogin(login)
}
