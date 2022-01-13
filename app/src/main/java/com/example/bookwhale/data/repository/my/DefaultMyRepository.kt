package com.example.bookwhale.data.repository.my

import android.util.Log
import com.example.bookwhale.data.entity.my.MyInfoEntity
import com.example.bookwhale.data.network.ServerApiService
import com.example.bookwhale.data.response.ErrorConverter
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.my.NickNameRequestDTO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody

class DefaultMyRepository(
    private val serverApiService: ServerApiService,
    private val ioDispatcher: CoroutineDispatcher
): MyRepository {
    override suspend fun getMyInfo(): NetworkResult<MyInfoEntity> = withContext(ioDispatcher) {
        val response = serverApiService.getMyInfo()

        if (response.isSuccessful) {
             NetworkResult.success(
                 MyInfoEntity(
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

        Log.e("여긴안왓어","뭐임")

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
}