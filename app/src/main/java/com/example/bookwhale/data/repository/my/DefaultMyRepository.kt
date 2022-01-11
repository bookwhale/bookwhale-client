package com.example.bookwhale.data.repository.my

import com.example.bookwhale.data.entity.my.MyInfoEntity
import com.example.bookwhale.data.network.ServerApiService
import com.example.bookwhale.data.response.ErrorConverter
import com.example.bookwhale.data.response.NetworkResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

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
}