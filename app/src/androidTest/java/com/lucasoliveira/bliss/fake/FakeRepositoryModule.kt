package com.lucasoliveira.bliss.fake

import com.lucasoliveira.bliss.data.repository.di.RepositoryModule
import com.lucasoliveira.bliss.domain.repository.AvatarRepository
import com.lucasoliveira.bliss.domain.repository.EmojiRepository
import com.lucasoliveira.bliss.domain.repository.RepoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [RepositoryModule::class])
interface FakeRepositoryModule {

    @Binds
    fun bindEmojiRepository(fakeEmojiRepository: FakeEmojiRepository): EmojiRepository

    @Binds
    fun bindAvatarRepository(fakeAvatarRepository: FakeAvatarRepository): AvatarRepository

    @Binds
    fun bindRepoRepository(fakeRepoRepository: FakeRepoRepository): RepoRepository
}
