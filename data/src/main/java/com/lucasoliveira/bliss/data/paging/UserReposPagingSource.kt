package com.lucasoliveira.bliss.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lucasoliveira.bliss.data.mapper.toDomain
import com.lucasoliveira.bliss.data.mapper.toRepositoryException
import com.lucasoliveira.bliss.data.remote.RemoteDataSource
import com.lucasoliveira.bliss.data.remote.network.GithubApiResponse
import com.lucasoliveira.bliss.domain.model.Repo

internal class UserReposPagingSource(
    private val remoteDataSource: RemoteDataSource,
    private val username: String,
    private val pageSize: Int,
) : PagingSource<Int, Repo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Repo> {
        val page = params.key ?: FIRST_PAGE

        return when (val response = remoteDataSource.getUserRepos(username, page, pageSize)) {
            is GithubApiResponse.Success -> {
                val repos = response.body
                LoadResult.Page(
                    data = repos.map { it.toDomain() },
                    prevKey = if (page == FIRST_PAGE) null else page - 1,
                    nextKey = if (repos.size < pageSize) null else page + 1,
                )
            }
            is GithubApiResponse.Error -> LoadResult.Error(response.error.toRepositoryException())
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Repo>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    companion object {
        const val FIRST_PAGE = 1
    }
}
