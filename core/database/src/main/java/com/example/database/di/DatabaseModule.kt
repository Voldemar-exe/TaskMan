package com.example.database.di

import android.content.Context
import com.example.database.TaskManDatabase
import com.example.database.dao.GroupDao
import com.example.database.dao.SyncDao
import com.example.database.dao.TaskDao
import com.example.shared.LocalDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    fun provideLocalDatabase(@ApplicationContext context: Context): LocalDatabase {
        return TaskManDatabase.getInstance(context)
    }

    @Provides
    fun provideTaskManDatabase(@ApplicationContext context: Context): TaskManDatabase {
        return TaskManDatabase.getInstance(context)
    }

    @Provides
    fun provideTaskDao(database: TaskManDatabase): TaskDao {
        return database.taskDao()
    }

    @Provides
    fun provideGroupDao(database: TaskManDatabase): GroupDao {
        return database.groupDao()
    }

    @Provides
    fun provideSyncDao(database: TaskManDatabase): SyncDao {
        return database.syncDao()
    }
}