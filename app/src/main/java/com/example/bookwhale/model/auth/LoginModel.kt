package com.example.bookwhale.model.auth

data class LoginModel(
    val apiToken: String? = null,
    val refreshToken: String? = null
)
