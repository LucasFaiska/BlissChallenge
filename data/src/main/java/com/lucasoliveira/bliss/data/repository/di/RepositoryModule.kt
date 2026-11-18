package com.lucasoliveira.bliss.data.repository.di

import com.lucasoliveira.bliss.data.repository.AvatarRepositoryImpl
import com.lucasoliveira.bliss.data.repository.EmojiRepositoryImpl
import com.lucasoliveira.bliss.domain.repository.AvatarRepository
import com.lucasoliveira.bliss.domain.repository.EmojiRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface RepositoryModule {

    @Binds
    @Singleton
    fun bindEmojiRepository(emojiRepositoryImpl: EmojiRepositoryImpl): EmojiRepository

    @Binds
    @Singleton
    fun bindAvatarRepository(avatarRepositoryImpl: AvatarRepositoryImpl): AvatarRepository
}
