package com.example.newzbreak.mvvm.room_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.newzbreak.common.ROOM_DB_NAME
import com.example.newzbreak.ui_layers.model.Article

@Database(entities = [Article::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class NewsRoomDb : RoomDatabase() {

    abstract fun newsDao(): NewsDao

    companion object {

        @Volatile
        private var instance: NewsRoomDb? = null
        private val LOCK = Any()

        fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also {
                instance = it
            }
        }

        private fun createDatabase(context: Context) = Room.databaseBuilder(context, NewsRoomDb::class.java, ROOM_DB_NAME).build()
    }
}