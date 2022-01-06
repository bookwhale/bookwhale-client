package com.example.bookwhale.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bookwhale.data.entity.home.RoomArticleEntity

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArticles(vararg articles: RoomArticleEntity)

    @Query("SELECT * FROM articles")
    fun getArticles(): List<RoomArticleEntity>

}