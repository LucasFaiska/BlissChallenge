package com.lucasoliveira.bliss.domain.usecase

import androidx.paging.PagingData
import com.lucasoliveira.bliss.domain.model.Repo
import com.lucasoliveira.bliss.domain.repository.RepoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGoogleReposUseCase @Inject constructor(
    private val repoRepository: RepoRepository
) {

    operator fun invoke(): Flow<PagingData<Repo>> = repoRepository.getUserRepos(GOOGLE_USERNAME)

    private companion object {
        const val GOOGLE_USERNAME = "google"
    }
}
