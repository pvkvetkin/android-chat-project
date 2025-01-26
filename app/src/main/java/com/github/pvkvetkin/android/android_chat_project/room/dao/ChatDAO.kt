package com.github.pvkvetkin.android.android_chat_project.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.pvkvetkin.android.android_chat_project.room.entity.ChatEntity

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats_or_channels WHERE owner = :username")
    suspend fun getUserChats(username: String): List<ChatEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChats(chats: List<ChatEntity>)
}