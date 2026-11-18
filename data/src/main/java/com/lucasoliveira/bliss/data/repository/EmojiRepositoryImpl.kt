package com.lucasoliveira.bliss.data.repository

import com.lucasoliveira.bliss.data.local.LocalDataSource
import com.lucasoliveira.bliss.data.mapper.toDomain
import com.lucasoliveira.bliss.data.mapper.toEmojiEntities
import com.lucasoliveira.bliss.data.mapper.toRepositoryException
import com.lucasoliveira.bliss.data.remote.RemoteDataSource
import com.lucasoliveira.bliss.data.remote.network.GithubApiResponse
import com.lucasoliveira.bliss.domain.model.Emoji
import com.lucasoliveira.bliss.domain.repository.EmojiRepository
import javax.inject.Inject

internal class EmojiRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
) : EmojiRepository {

    override suspend fun getEmojis(): Result<List<Emoji>> {
        if (localDataSource.countEmojis() > 0) {
            return Result.success(cachedEmojis())
        }

        return when (val response = remoteDataSource.getEmojis()) {
            is GithubApiResponse.Success -> {
                localDataSource.insertEmojis(response.body.toEmojiEntities())
                Result.success(cachedEmojis())
            }
            is GithubApiResponse.Error -> Result.failure(response.error.toRepositoryException())
        }
    }

    private suspend fun cachedEmojis(): List<Emoji> =
        localDataSource.getEmojis().map { it.toDomain() }
}
