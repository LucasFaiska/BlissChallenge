package com.lucasoliveira.bliss.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emojis")
internal data class EmojiEntity(
    @PrimaryKey val name: String,
    val url: String,
)
