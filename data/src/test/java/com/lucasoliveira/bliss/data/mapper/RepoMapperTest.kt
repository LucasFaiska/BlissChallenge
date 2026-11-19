package com.lucasoliveira.bliss.data.mapper

import com.lucasoliveira.bliss.data.remote.dto.RepoResponseDto
import com.lucasoliveira.bliss.domain.model.Repo
import org.junit.Assert.assertEquals
import org.junit.Test

class RepoMapperTest {

    @Test
    fun `given repo response, when toDomain is called, then return repo with same values`() {
        val dto = RepoResponseDto(id = 170908616L, fullName = "google/.github", isPrivate = false)

        assertEquals(Repo(id = 170908616L, fullName = "google/.github", isPrivate = false), dto.toDomain())
    }
}
