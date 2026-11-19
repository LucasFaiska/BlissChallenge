package com.lucasoliveira.bliss.data.mapper

import com.lucasoliveira.bliss.data.remote.dto.RepoResponseDto
import com.lucasoliveira.bliss.domain.model.Repo

internal fun RepoResponseDto.toDomain(): Repo =
    Repo(id = id, fullName = fullName, isPrivate = isPrivate)
