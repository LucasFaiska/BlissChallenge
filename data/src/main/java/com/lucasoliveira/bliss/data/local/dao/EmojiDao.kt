package com.lucasoliveira.bliss.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lucasoliveira.bliss.data.local.entity.EmojiEntity

@Dao
internal interface EmojiDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(emojis: List<EmojiEntity>)

    @Query("SELECT * FROM emojis ORDER BY name ASC")
    suspend fun getAll(): List<EmojiEntity>

    @Query("SELECT COUNT(*) FROM emojis")
    suspend fun count(): Int
}
