package com.example.bookwhale.data.entity.home

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class RoomArticleEntity (
    @PrimaryKey(autoGenerate = true) val id: Int = -1,
    val articleId: Int,
    val articleImage : String,
    val articleTitle : String,
    val articlePrice : String,
    val bookStatus : String,
    val sellingLocation : String,
    val chatCount : Int,
    val favoriteCount : Int,
    val beforeTime : String
)
