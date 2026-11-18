package com.lucasoliveira.bliss.data.repository

import com.lucasoliveira.bliss.data.local.LocalDataSource
import com.lucasoliveira.bliss.data.mapper.toDomain
import com.lucasoliveira.bliss.data.mapper.toEntity
import com.lucasoliveira.bliss.data.mapper.toRepositoryException
import com.lucasoliveira.bliss.data.remote.RemoteDataSource
import com.lucasoliveira.bliss.data.remote.network.GithubApiResponse
import com.lucasoliveira.bliss.domain.model.Avatar
import com.lucasoliveira.bliss.domain.repository.AvatarRepository
import javax.inject.Inject

internal class AvatarRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
) : AvatarRepository {

    override suspend fun getAvatar(login: String): Result<Avatar> {
        localDataSource.getAvatarByLogin(login)?.let { cached ->
            return Result.success(cached.toDomain())
        }

        return when (val response = remoteDataSource.getUser(login)) {
            is GithubApiResponse.Success -> {
                val entity = response.body.toEntity()
                localDataSource.insertAvatar(entity)
                Result.success(entity.toDomain())
            }
            is GithubApiResponse.Error -> Result.failure(
                response.error.toRepositoryException(notFoundMessage = USER_NOT_FOUND_MESSAGE.format(login))
            )
        }
    }

    override suspend fun getAvatars(): Result<List<Avatar>> =
        Result.success(localDataSource.getAvatars().map { it.toDomain() })

    override suspend fun deleteAvatar(login: String): Result<Unit> =
        Result.success(localDataSource.deleteAvatarByLogin(login))

    private companion object {
        const val USER_NOT_FOUND_MESSAGE = "User %s not found"
    }
}
