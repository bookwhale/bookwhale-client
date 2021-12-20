package com.example.bookwhale.data.repository.main.my

import com.example.bookwhale.data.entity.my.ProfileEntity

interface MyRepository {

    suspend fun getProfile() : ProfileEntity

}