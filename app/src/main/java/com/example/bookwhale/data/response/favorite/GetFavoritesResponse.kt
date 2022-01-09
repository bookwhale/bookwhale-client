package com.example.bookwhale.data.response.favorite

import com.example.bookwhale.data.response.ErrorResponse
import com.example.bookwhale.data.response.home.GetAllArticlesResponse

data class GetFavoritesResponse(
    val favoriteId: Int,
    val articlesResponse: GetAllArticlesResponse
): ErrorResponse()
