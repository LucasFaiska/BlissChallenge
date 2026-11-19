package com.lucasoliveira.bliss.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class RepoResponseDto(
    @SerialName("id") val id: Long,
    @SerialName("full_name") val fullName: String,
    @SerialName("private") val isPrivate: Boolean,
)
