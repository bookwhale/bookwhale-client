package com.example.bookwhale.data.response

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ErrorConverter {
    fun convert(errorString: String){
        val type = object : TypeToken<ErrorResponse?>() {}.type
        val responseError: ErrorResponse = Gson().fromJson(errorString, type)
    }
}

