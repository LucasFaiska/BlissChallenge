package com.lucasoliveira.bliss.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "avatars")
internal data class AvatarEntity(
    @PrimaryKey
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val login: String,
    val id: Long,
    val avatarUrl: String,
)
