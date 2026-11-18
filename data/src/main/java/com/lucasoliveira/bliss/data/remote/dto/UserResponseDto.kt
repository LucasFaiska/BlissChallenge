package com.lucasoliveira.bliss.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class UserResponseDto(
    @SerialName("login") val login: String,
    @SerialName("id") val id: Long,
    @SerialName("avatar_url") val avatarUrl: String,
)
