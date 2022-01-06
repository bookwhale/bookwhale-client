package com.example.bookwhale.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bookwhale.data.entity.home.RoomArticleEntity
import com.example.bookwhale.data.db.dao.ArticleDao

@Database(
    entities =[RoomArticleEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDataBase: RoomDatabase() {

    companion object {
        const val DB_NAME = "AppDataBase.db"
    }

    abstract fun ArticleDao(): ArticleDao
}

//private lateinit var INSTANCE: AppDataBase
//fun getArticlesDatabase(context: Context): AppDataBase {
//    synchronized(AppDataBase::class.java) {
//        if(!::INSTANCE.isInitialized) {
//            INSTANCE = Room.databaseBuilder(context.applicationContext,
//                AppDataBase::class.java, "articles")
//                .fallbackToDestructiveMigration()
//                .build()
//        }
//        return INSTANCE
//    }
//}