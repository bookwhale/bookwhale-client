package com.example.bookwhale.data.response.login

import com.google.gson.annotations.SerializedName

data class TokenRequestDTO(
    @SerializedName("apiToken")
    private val apiToken: String,
    @SerializedName("refreshToken")
    private val refreshToken: String
)
