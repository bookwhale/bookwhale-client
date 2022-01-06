package com.example.bookwhale.data.db

import android.content.Context
import androidx.room.Room

fun provideDB(context: Context): AppDataBase =
    Room.databaseBuilder(context, AppDataBase::class.java, AppDataBase.DB_NAME).build()

fun provideArticleDao(database: AppDataBase) = database.ArticleDao()