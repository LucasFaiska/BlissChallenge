package com.lucasoliveira.bliss.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lucasoliveira.bliss.data.local.entity.AvatarEntity

@Dao
internal interface AvatarDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(avatar: AvatarEntity)

    @Query("SELECT * FROM avatars WHERE login = :login")
    suspend fun getByLogin(login: String): AvatarEntity?

    @Query("SELECT * FROM avatars ORDER BY login ASC")
    suspend fun getAll(): List<AvatarEntity>

    @Query("DELETE FROM avatars WHERE login = :login")
    suspend fun deleteByLogin(login: String)
}
