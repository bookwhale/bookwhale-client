package com.example.bookwhale.data.response.article

data class GetNaverBookApiResponse(
    var bookIsbn: String,
    var bookTitle: String,
    val bookAuthor: String,
    val bookPublisher: String,
    val bookThumbnail: String,
    val bookListPrice: String,
    val bookPubDate: String,
    val bookSummary: String
)
