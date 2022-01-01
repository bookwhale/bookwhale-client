package com.example.bookwhale.data.repository.main.my

import com.example.bookwhale.data.entity.my.ProfileEntity
import com.example.bookwhale.data.network.ServerApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DefaultMyRepository(
    private val serverApiService: ServerApiService,
    private val ioDispatcher: CoroutineDispatcher
): MyRepository {

    override suspend fun getProfile(): ProfileEntity = withContext(ioDispatcher) {

        return@withContext ProfileEntity(
                profileImage = "",
                profileName = "안녕하세요"

        )

    }
}