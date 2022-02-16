package com.example.bookwhale.data.repository.my

import com.example.bookwhale.data.entity.my.MyInfoEntity
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.my.NickNameRequestDTO
import okhttp3.MultipartBody

interface MyRepository {

    suspend fun getMyInfo() : NetworkResult<MyInfoEntity>

    suspend fun updateMyNickName(nickNameRequestDTO: NickNameRequestDTO) : NetworkResult<Boolean>

    suspend fun updateProfileImage(profileImage : MultipartBody.Part) : NetworkResult<Boolean>

    suspend fun logOut() : NetworkResult<Boolean>

    suspend fun withDraw() : NetworkResult<Boolean>
}