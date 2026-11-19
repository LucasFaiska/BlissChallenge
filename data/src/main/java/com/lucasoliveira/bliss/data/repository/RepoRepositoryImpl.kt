package com.lucasoliveira.bliss.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.lucasoliveira.bliss.data.paging.UserReposPagingSource
import com.lucasoliveira.bliss.data.remote.RemoteDataSource
import com.lucasoliveira.bliss.domain.model.Repo
import com.lucasoliveira.bliss.domain.repository.RepoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class RepoRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
) : RepoRepository {

    override fun getUserRepos(username: String): Flow<PagingData<Repo>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = { UserReposPagingSource(remoteDataSource, username, PAGE_SIZE) },
        ).flow

    companion object {
        const val PAGE_SIZE = 100
    }
}
