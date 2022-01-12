package com.example.bookwhale.data.repository.my

import com.example.bookwhale.data.entity.my.MyInfoEntity
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.my.NickNameRequestDTO

interface MyRepository {

    suspend fun getMyInfo() : NetworkResult<MyInfoEntity>

    suspend fun updateMyNickName(nickNameRequestDTO: NickNameRequestDTO) : NetworkResult<Boolean>
}