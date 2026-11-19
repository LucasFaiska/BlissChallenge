package com.lucasoliveira.bliss.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.lucasoliveira.bliss.presentation.avatarlist.AvatarListRoot
import com.lucasoliveira.bliss.presentation.emojilist.EmojiListRoot
import com.lucasoliveira.bliss.presentation.googlerepos.GoogleReposRoot
import com.lucasoliveira.bliss.presentation.home.HomeRoot

@Composable
fun BlissNavDisplay(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(HomeRoute)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<HomeRoute> {
                HomeRoot(
                    onNavigateToEmojiList = { backStack.add(EmojiListRoute) },
                    onNavigateToAvatarList = { backStack.add(AvatarListRoute) },
                    onNavigateToGoogleRepos = { backStack.add(GoogleReposRoute) },
                )
            }
            entry<EmojiListRoute> { EmojiListRoot() }
            entry<AvatarListRoute> { AvatarListRoot() }
            entry<GoogleReposRoute> { GoogleReposRoot() }
        },
        modifier = modifier,
    )
}
