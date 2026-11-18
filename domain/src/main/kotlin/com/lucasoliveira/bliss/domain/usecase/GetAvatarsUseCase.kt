package com.lucasoliveira.bliss.domain.usecase

import com.lucasoliveira.bliss.domain.error.DomainException
import com.lucasoliveira.bliss.domain.model.Avatar
import com.lucasoliveira.bliss.domain.repository.AvatarRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAvatarsUseCase @Inject constructor(
    private val avatarRepository: AvatarRepository
) {

    operator fun invoke(): Flow<List<Avatar>> = flow {
        emit(
            avatarRepository.getAvatars().getOrElse { error ->
                throw DomainException(error.message, error)
            }
        )
    }
}
