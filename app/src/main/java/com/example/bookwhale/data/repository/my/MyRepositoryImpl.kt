package com.example.bookwhale.data.repository.my

import com.example.bookwhale.model.main.my.MyInfoModel
import com.example.bookwhale.data.network.ServerApiService
import com.example.bookwhale.data.response.ErrorConverter
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.my.LogOutDTO
import com.example.bookwhale.data.response.my.NickNameRequestDTO
import com.example.bookwhale.model.main.my.NotiModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody

class MyRepositoryImpl(
    private val serverApiService: ServerApiService,
    private val ioDispatcher: CoroutineDispatcher
): MyRepository {
    override suspend fun getMyInfo(): NetworkResult<MyInfoModel> = withContext(ioDispatcher) {
        val response = serverApiService.getMyInfo()

        if (response.isSuccessful) {
             NetworkResult.success(
                 MyInfoModel(
                     userId = response.body()!!.userId,
                     email = response.body()!!.email,
                     nickName = response.body()!!.nickName,
                     profileImage = response.body()!!.profileImage
                 )
             )
        } else {
            val errorCode = ErrorConverter.convert(response.errorBody()?.string())
            NetworkResult.error(code = errorCode)
        }
    }

    override suspend fun updateMyNickName(nickNameRequestDTO: NickNameRequestDTO): NetworkResult<Boolean> = withContext(ioDispatcher) {
        val response = serverApiService.updateMyNickName(nickNameRequestDTO)

        if(response.isSuccessful) {
            NetworkResult.success(
                true
            )
        } else {
            val errorCode = ErrorConverter.convert(response.errorBody()?.string())
            NetworkResult.error(code = errorCode)
        }
    }

    override suspend fun updateProfileImage(profileImage: MultipartBody.Part): NetworkResult<Boolean> = withContext(ioDispatcher) {

        val response = serverApiService.updateProfile(profileImage)

        if(response.isSuccessful) {
            NetworkResult.success(
                true
            )
        } else {
            val errorCode = ErrorConverter.convert(response.errorBody()?.string())
            NetworkResult.error(code = errorCode)
        }
    }

    override suspend fun logOut(logOutDTO: LogOutDTO): NetworkResult<Boolean> = withContext(ioDispatcher) {
        val response = serverApiService.logOut(logOutDTO)

        if(response.isSuccessful) {
            NetworkResult.success(
                true
            )
        } else {
            val errorCode = ErrorConverter.convert(response.errorBody()?.string())
            NetworkResult.error(code = errorCode)
        }
    }

    override suspend fun withDraw(logOutDTO: LogOutDTO): NetworkResult<Boolean> = withContext(ioDispatcher) {
        val response = serverApiService.withDraw(logOutDTO)

        if(response.isSuccessful) {
            NetworkResult.success(
                true
            )
        } else {
            val errorCode = ErrorConverter.convert(response.errorBody()?.string())
            NetworkResult.error(code = errorCode)
        }
    }

    override suspend fun getNotiSetting(): NetworkResult<NotiModel> = withContext(ioDispatcher) {
        val response = serverApiService.getNotiSetting()

        if(response.isSuccessful) {
            NetworkResult.success(
                NotiModel(
                    userId = response.body()!!.userId,
                    pushActivate = response.body()!!.pushActivate
                )
            )
        } else {
            val errorCode = ErrorConverter.convert(response.errorBody()?.string())
            NetworkResult.error(code = errorCode)
        }
    }

    override suspend fun toggleNotiSetting(): NetworkResult<Boolean>  = withContext(ioDispatcher) {
        val response = serverApiService.toggleNotiSetting()

        if(response.isSuccessful) {
            NetworkResult.success(
                true
            )
        } else {
            val errorCode = ErrorConverter.convert(response.errorBody()?.string())
            NetworkResult.error(code = errorCode)
        }
    }
}