package com.example.bookwhale.data.response.article

data class BookResponse(
    val bookIsbn: String,
    val bookTitle: String,
    val bookAuthor: String,
    val bookPublisher: String,
    val bookThumbnail: String,
    val bookListPrice: String,
    val bookPubDate: String,
    val bookSummary: String
)
