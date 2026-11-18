package com.lucasoliveira.bliss.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lucasoliveira.bliss.data.local.dao.AvatarDao
import com.lucasoliveira.bliss.data.local.dao.EmojiDao
import com.lucasoliveira.bliss.data.local.entity.AvatarEntity
import com.lucasoliveira.bliss.data.local.entity.EmojiEntity

@Database(
    entities = [
        EmojiEntity::class,
        AvatarEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
internal abstract class BlissDatabase : RoomDatabase() {

    abstract fun emojiDao(): EmojiDao

    abstract fun avatarDao(): AvatarDao
}
