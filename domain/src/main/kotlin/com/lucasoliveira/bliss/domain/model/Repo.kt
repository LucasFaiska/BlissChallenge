package com.lucasoliveira.bliss.domain.model

data class Repo(
    val id: Long,
    val fullName: String,
    val isPrivate: Boolean,
)
