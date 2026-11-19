package com.lucasoliveira.bliss.domain.repository

import androidx.paging.PagingData
import com.lucasoliveira.bliss.domain.model.Repo
import kotlinx.coroutines.flow.Flow

interface RepoRepository {

    fun getUserRepos(username: String): Flow<PagingData<Repo>>
}
