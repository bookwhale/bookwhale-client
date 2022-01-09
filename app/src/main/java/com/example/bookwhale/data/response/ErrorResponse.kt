package com.example.bookwhale.data.response


open class ErrorResponse {
    var status: Int? = null
    var message: String? = null
    var code: String? = null
    var errors: String? = null
}