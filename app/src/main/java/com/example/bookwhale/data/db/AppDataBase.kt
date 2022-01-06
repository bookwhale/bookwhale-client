package com.example.bookwhale.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bookwhale.data.entity.home.ArticleEntity
import com.example.bookwhale.data.db.dao.ArticleDao

@Database(
    entities =[ArticleEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDataBase: RoomDatabase() {

    companion object {
        const val DB_NAME = "AppDataBase.db"
    }

    abstract fun ArticleDao(): ArticleDao
}