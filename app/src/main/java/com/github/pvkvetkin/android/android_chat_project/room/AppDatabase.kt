package com.github.pvkvetkin.android.android_chat_project.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.pvkvetkin.android.android_chat_project.R
import com.github.pvkvetkin.android.android_chat_project.room.dao.ChatDao
import com.github.pvkvetkin.android.android_chat_project.room.dao.MessageDao
import com.github.pvkvetkin.android.android_chat_project.room.entity.ChatEntity
import com.github.pvkvetkin.android.android_chat_project.room.entity.MessageEntity

@Database(entities = [MessageEntity::class, ChatEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    context.getString(R.string.app_db_name)
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
