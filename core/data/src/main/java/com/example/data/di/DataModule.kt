package com.example.data.di

import com.example.data.repository.HistoryRepository
import com.example.data.repository.HistoryRepositoryImpl
import com.example.data.repository.SessionRepository
import com.example.data.repository.SessionRepositoryImpl
import com.example.data.repository.ThemeRepository
import com.example.data.repository.ThemeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindHistoryRepository(
        historyRepositoryImpl: HistoryRepositoryImpl
    ): HistoryRepository

    @Binds
    abstract fun bindSessionRepository(
        sessionRepositoryImpl: SessionRepositoryImpl
    ): SessionRepository

    @Binds
    abstract fun bindThemeRepository(
        themeRepositoryImpl: ThemeRepositoryImpl
    ): ThemeRepository
}