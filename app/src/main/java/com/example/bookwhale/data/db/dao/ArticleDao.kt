package com.example.bookwhale.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bookwhale.data.entity.home.ArticleEntity

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArticles(vararg articles: ArticleEntity)

    @Query("SELECT * FROM articles")
    fun getArticles(): List<ArticleEntity>

}