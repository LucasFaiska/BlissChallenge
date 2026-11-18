package com.lucasoliveira.bliss.domain.usecase

import com.lucasoliveira.bliss.domain.error.DomainException
import com.lucasoliveira.bliss.domain.repository.AvatarRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteAvatarUseCase @Inject constructor(
    private val avatarRepository: AvatarRepository
) {

    operator fun invoke(login: String): Flow<Unit> = flow {
        emit(
            avatarRepository.deleteAvatar(login).getOrElse { error ->
                throw DomainException(error.message, error)
            }
        )
    }
}
