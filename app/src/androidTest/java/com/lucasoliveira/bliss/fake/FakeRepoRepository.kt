package com.lucasoliveira.bliss.fake

import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import com.lucasoliveira.bliss.domain.model.Repo
import com.lucasoliveira.bliss.domain.repository.RepoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeRepoRepository @Inject constructor() : RepoRepository {

    private val complete = LoadState.NotLoading(endOfPaginationReached = true)

    private val repos = mapOf(
        "google" to listOf(
            Repo(id = 460600860L, fullName = "google/.allstar", isPrivate = false),
            Repo(id = 170908616L, fullName = "google/.github", isPrivate = false),
        )
    )

    override fun getUserRepos(username: String): Flow<PagingData<Repo>> =
        flowOf(PagingData.from(repos[username].orEmpty(), LoadStates(complete, complete, complete)))
}
