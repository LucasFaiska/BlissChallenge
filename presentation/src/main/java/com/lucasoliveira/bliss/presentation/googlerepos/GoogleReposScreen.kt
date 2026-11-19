package com.lucasoliveira.bliss.presentation.googlerepos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.lucasoliveira.bliss.domain.model.Repo
import com.lucasoliveira.bliss.presentation.R
import com.lucasoliveira.bliss.presentation.component.BlissActionButton
import com.lucasoliveira.bliss.presentation.component.BlissEmptyMessage
import com.lucasoliveira.bliss.presentation.component.BlissErrorView
import com.lucasoliveira.bliss.presentation.component.BlissLoading
import com.lucasoliveira.bliss.presentation.theme.BlissTheme
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleReposScreen(repos: LazyPagingItems<Repo>) {
    val refreshState = repos.loadState.refresh

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding(),
    ) {
        when {
            refreshState is LoadState.Loading && repos.itemCount == 0 -> BlissLoading(modifier = Modifier.fillMaxSize())
            refreshState is LoadState.Error && repos.itemCount == 0 -> BlissErrorView(
                title = stringResource(R.string.error_title),
                message = refreshState.error.message ?: stringResource(R.string.error_generic),
                retryLabel = stringResource(R.string.error_retry),
                onRetry = repos::retry,
                modifier = Modifier.fillMaxSize(),
            )
            refreshState is LoadState.NotLoading && repos.itemCount == 0 -> BlissEmptyMessage(
                message = stringResource(R.string.repos_empty),
                modifier = Modifier.align(Alignment.Center),
            )
            else -> PullToRefreshBox(
                isRefreshing = refreshState is LoadState.Loading,
                onRefresh = repos::refresh,
                modifier = Modifier.fillMaxSize(),
            ) {
                RepoList(repos = repos)
            }
        }
    }
}

@Composable
private fun RepoList(repos: LazyPagingItems<Repo>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(count = repos.itemCount, key = repos.itemKey { it.id }) { index ->
            repos[index]?.let { repo -> RepoRow(repo = repo) }
        }
        item {
            when (val appendState = repos.loadState.append) {
                is LoadState.Loading -> BlissLoading(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                )
                is LoadState.Error -> AppendError(
                    message = appendState.error.message ?: stringResource(R.string.error_generic),
                    onRetry = repos::retry,
                )
                is LoadState.NotLoading -> Unit
            }
        }
    }
}

@Composable
private fun RepoRow(repo: Repo) {
    Text(
        text = repo.fullName,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
    )
}

@Composable
private fun AppendError(message: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Text(text = message, color = MaterialTheme.colorScheme.onBackground)
        BlissActionButton(text = stringResource(R.string.error_retry), onClick = onRetry)
    }
}

private val previewRepos = listOf(
    "google/.github", "google/0x0g-2018-badge", "google/abpackage", "google/abstreet", "google/acai",
    "google/access-bridge-explorer", "google/Accessibility-Test-Framework-for-Android",
    "google/account-provisioning-for-google-apps", "google/acme", "google/active-learning",
).mapIndexed { index, name -> Repo(id = index.toLong(), fullName = name, isPrivate = false) }

private val notLoading = LoadState.NotLoading(endOfPaginationReached = false)

@Composable
private fun previewItems(repos: List<Repo>, refresh: LoadState, append: LoadState = notLoading): LazyPagingItems<Repo> =
    flowOf(
        PagingData.from(
            data = repos,
            sourceLoadStates = LoadStates(refresh = refresh, prepend = notLoading, append = append),
        )
    ).collectAsLazyPagingItems()

@Preview(showSystemUi = true)
@Composable
private fun GoogleReposScreenContentPreview() {
    BlissTheme {
        GoogleReposScreen(repos = previewItems(previewRepos, refresh = notLoading))
    }
}

@Preview(showSystemUi = true)
@Composable
private fun GoogleReposScreenLoadingPreview() {
    BlissTheme {
        GoogleReposScreen(repos = previewItems(emptyList(), refresh = LoadState.Loading))
    }
}

@Preview(showSystemUi = true)
@Composable
private fun GoogleReposScreenErrorPreview() {
    BlissTheme {
        GoogleReposScreen(
            repos = previewItems(
                emptyList(),
                refresh = LoadState.Error(IllegalStateException("API rate limit reached. Try again at 14:32")),
            )
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun GoogleReposScreenAppendErrorPreview() {
    BlissTheme {
        GoogleReposScreen(
            repos = previewItems(
                previewRepos,
                refresh = notLoading,
                append = LoadState.Error(IllegalStateException("No internet connection")),
            )
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun GoogleReposScreenEmptyPreview() {
    BlissTheme {
        GoogleReposScreen(repos = previewItems(emptyList(), refresh = LoadState.NotLoading(endOfPaginationReached = true)))
    }
}
