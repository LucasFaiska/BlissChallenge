package com.lucasoliveira.bliss.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute : NavKey

@Serializable
data object EmojiListRoute : NavKey

@Serializable
data object AvatarListRoute : NavKey
