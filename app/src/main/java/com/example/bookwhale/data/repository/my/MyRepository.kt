package com.example.bookwhale.data.repository.my

import com.example.bookwhale.model.main.my.MyInfoModel
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.my.LogOutDTO
import com.example.bookwhale.data.response.my.NickNameRequestDTO
import com.example.bookwhale.model.main.my.NotiModel
import okhttp3.MultipartBody

interface MyRepository {

    suspend fun getMyInfo() : NetworkResult<MyInfoModel>

    suspend fun updateMyNickName(nickNameRequestDTO: NickNameRequestDTO) : NetworkResult<Boolean>

    suspend fun updateProfileImage(profileImage : MultipartBody.Part) : NetworkResult<Boolean>

    suspend fun logOut(logOutDTO: LogOutDTO) : NetworkResult<Boolean>

    suspend fun withDraw(logOutDTO: LogOutDTO) : NetworkResult<Boolean>

    suspend fun getNotiSetting() : NetworkResult<NotiModel>

    suspend fun toggleNotiSetting() : NetworkResult<Boolean>
}