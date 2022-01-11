package com.example.bookwhale.data.repository.my

import com.example.bookwhale.data.entity.my.MyInfoEntity
import com.example.bookwhale.data.response.NetworkResult

interface MyRepository {

    suspend fun getMyInfo() : NetworkResult<MyInfoEntity>
}