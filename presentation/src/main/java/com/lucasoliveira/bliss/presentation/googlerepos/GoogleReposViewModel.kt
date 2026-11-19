package com.lucasoliveira.bliss.presentation.googlerepos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.lucasoliveira.bliss.domain.model.Repo
import com.lucasoliveira.bliss.domain.usecase.GetGoogleReposUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class GoogleReposViewModel @Inject constructor(
    getGoogleReposUseCase: GetGoogleReposUseCase,
) : ViewModel() {

    val repos: Flow<PagingData<Repo>> = getGoogleReposUseCase().cachedIn(viewModelScope)
}
