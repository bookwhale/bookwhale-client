package com.example.bookwhale.model.article

import android.os.Parcelable
import com.example.bookwhale.model.CellType
import com.example.bookwhale.model.Model
import kotlinx.parcelize.Parcelize

@Parcelize
data class NaverBookModel(
    var bookIsbn : String,
    var bookTitle : String,
    val bookAuthor : String,
    val bookPublisher : String,
    val bookThumbnail: String,
    val bookListPrice : String,
    val bookPubDate : String?,
    val bookSummary : String
): Parcelable
