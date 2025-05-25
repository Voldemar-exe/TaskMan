package com.example.taskman.di

import android.content.Context
import android.content.SharedPreferences
import com.example.taskman.api.RetrofitClient
import com.example.taskman.api.auth.AuthClient
import com.example.taskman.api.profile.ProfileClient
import com.example.taskman.api.sync.SyncClient
import com.example.taskman.api.sync.SyncRepository
import com.example.taskman.db.GroupDao
import com.example.taskman.db.TaskDao
import com.example.taskman.db.TaskManDatabase
import com.example.taskman.ui.auth.AuthDataStore
import com.example.taskman.ui.auth.AuthStorage
import com.example.taskman.ui.utils.HistoryRepository
import com.example.taskman.ui.utils.HistoryRepositoryImpl
import com.example.taskman.ui.utils.SessionRepository
import com.example.taskman.ui.utils.SessionRepositoryImpl
import com.example.taskman.ui.utils.ThemeRepository
import com.example.taskman.ui.utils.ThemeRepositoryImpl
import org.koin.dsl.module

val dataModule = module {
    single { RetrofitClient.init(get<AuthStorage>()) }
    single { get<RetrofitClient>().let { AuthClient.instance } }
    single { get<RetrofitClient>().let { ProfileClient.instance } }
    single { get<RetrofitClient>().let { SyncClient.instance } }

    single<SharedPreferences> {
        get<Context>().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    single<TaskManDatabase> { TaskManDatabase.getInstance(get()) }
    single<TaskDao> { get<TaskManDatabase>().taskDao() }
    single<GroupDao> { get<TaskManDatabase>().groupDao() }

    single<ThemeRepository> { ThemeRepositoryImpl(get()) }
    single<HistoryRepository> { HistoryRepositoryImpl(get()) }
    single<SessionRepository> { SessionRepositoryImpl(get(), get()) }
    single<SyncRepository> { SyncRepository(get(), get(), get()) }

    single<AuthStorage> { AuthDataStore(get()) }
}


