package com.example.datastore.di

import android.content.Context
import com.example.datastore.AuthDataSource
import com.example.datastore.AuthDataStore
import com.example.datastore.HistoryDataSource
import com.example.datastore.HistoryDatastore
import com.example.datastore.ThemeDataSource
import com.example.datastore.ThemeDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    fun provideAuthDataSource(@ApplicationContext context: Context): AuthDataSource {
        return AuthDataStore(context)
    }

    @Provides
    fun provideThemeDataSource(@ApplicationContext context: Context): ThemeDataSource {
        return ThemeDataStore(context)
    }

    @Provides
    fun provideHistoryDataSource(@ApplicationContext context: Context): HistoryDataSource {
        return HistoryDatastore(context)
    }
}