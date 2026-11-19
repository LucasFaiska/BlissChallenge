package com.lucasoliveira.bliss.presentation.googlerepos

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems

@Composable
fun GoogleReposRoot(
    viewModel: GoogleReposViewModel = hiltViewModel(),
) {
    GoogleReposScreen(repos = viewModel.repos.collectAsLazyPagingItems())
}
