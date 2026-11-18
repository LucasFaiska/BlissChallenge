package com.lucasoliveira.bliss.data.mapper

import com.lucasoliveira.bliss.data.local.entity.EmojiEntity
import com.lucasoliveira.bliss.domain.model.Emoji

internal fun Map<String, String>.toEmojiEntities(): List<EmojiEntity> =
    map { (name, url) -> EmojiEntity(name = name, url = url) }

internal fun EmojiEntity.toDomain(): Emoji = Emoji(name = name, url = url)
