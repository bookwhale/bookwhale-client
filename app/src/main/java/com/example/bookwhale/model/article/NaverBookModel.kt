package com.example.bookwhale.model.article

import android.os.Parcelable
import com.example.bookwhale.model.CellType
import com.example.bookwhale.model.Model
import kotlinx.parcelize.Parcelize

@Parcelize
data class NaverBookModel(
    var bookIsbn : String,
    var bookTitle : String,
    var bookAuthor : String,
    var bookPublisher : String,
    var bookThumbnail: String,
    var bookListPrice : String,
    var bookPubDate : String?,
    var bookSummary : String
): Parcelable {
    constructor(): this("","","","","","","","")
}
