package com.example.bookwhale.data.response.article

data class ArticleDTO(
    val bookRequest: BookRequest,
    val title: String,
    val price: String,
    val description: String,
    val bookStatus: String,
    val sellingLocation: String

) {
    data class BookRequest(
        var bookIsbn: String,
        var bookTitle: String,
        val bookAuthor: String,
        val bookPublisher: String,
        val bookThumbnail: String,
        val bookListPrice: String,
        val bookPubDate: String?,
        val bookSummary: String
    )
}
