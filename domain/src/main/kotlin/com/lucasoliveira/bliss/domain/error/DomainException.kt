package com.lucasoliveira.bliss.domain.error

class DomainException(
    message: String?,
    cause: Throwable? = null,
) : Exception(message, cause)
