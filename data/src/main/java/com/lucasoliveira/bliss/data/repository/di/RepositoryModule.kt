package com.lucasoliveira.bliss.data.repository.di

import com.lucasoliveira.bliss.data.repository.AvatarRepositoryImpl
import com.lucasoliveira.bliss.data.repository.EmojiRepositoryImpl
import com.lucasoliveira.bliss.data.repository.RepoRepositoryImpl
import com.lucasoliveira.bliss.domain.repository.AvatarRepository
import com.lucasoliveira.bliss.domain.repository.EmojiRepository
import com.lucasoliveira.bliss.domain.repository.RepoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    internal abstract fun bindEmojiRepository(emojiRepositoryImpl: EmojiRepositoryImpl): EmojiRepository

    @Binds
    @Singleton
    internal abstract fun bindAvatarRepository(avatarRepositoryImpl: AvatarRepositoryImpl): AvatarRepository

    @Binds
    @Singleton
    internal abstract fun bindRepoRepository(repoRepositoryImpl: RepoRepositoryImpl): RepoRepository
}
