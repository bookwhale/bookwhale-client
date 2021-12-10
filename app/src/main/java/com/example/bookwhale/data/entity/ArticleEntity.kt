package com.example.bookwhale.data.entity

data class ArticleEntity (
    val postId : Long,
    val postImage : String,
    val postTitle : String,
    val postPrice : String,
    val postStatus: String,
    val bookTitle: String,
    val bookAuthor: String,
    val bookPublisher : String,
    val sellingLocation : String,
    val viewCount : Long,
    val likeCount : Long,
    val beforeTime : String
    )