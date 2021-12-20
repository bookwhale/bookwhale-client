package com.example.bookwhale.data.repository.main.my

import com.example.bookwhale.data.entity.my.ProfileEntity
import com.example.bookwhale.data.network.ArticleApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DefaultMyRepository(
    private val articleApiService: ArticleApiService,
    private val ioDispatcher: CoroutineDispatcher
): MyRepository {

    override suspend fun getProfile(): ProfileEntity = withContext(ioDispatcher) {

        return@withContext ProfileEntity(
                profileImage = "",
                profileName = "안녕하세요"

        )

    }
}