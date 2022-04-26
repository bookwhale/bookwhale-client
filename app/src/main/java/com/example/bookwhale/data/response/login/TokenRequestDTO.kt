package com.example.bookwhale.data.response.login

import com.google.gson.annotations.SerializedName

data class TokenRequestDTO(
    @SerializedName("apiToken")
    val apiToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String
)
