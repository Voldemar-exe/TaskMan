package com.example.taskman.di

import android.content.Context
import android.content.SharedPreferences
import androidx.work.WorkManager
import com.example.taskman.api.RetrofitClient
import com.example.taskman.api.auth.AuthClient
import com.example.taskman.api.auth.AuthService
import com.example.taskman.api.profile.ProfileClient
import com.example.taskman.api.profile.ProfileService
import com.example.taskman.api.sync.SyncClient
import com.example.taskman.api.sync.SyncRepository
import com.example.taskman.api.sync.SyncService
import com.example.taskman.db.GroupDao
import com.example.taskman.db.TaskDao
import com.example.taskman.db.TaskManDatabase
import com.example.taskman.ui.auth.AuthDataStore
import com.example.taskman.ui.auth.AuthStorage
import com.example.taskman.ui.auth.AuthViewModel
import com.example.taskman.ui.control.group.GroupControlViewModel
import com.example.taskman.ui.control.task.TaskControlViewModel
import com.example.taskman.ui.main.MainViewModel
import com.example.taskman.ui.profile.ProfileViewModel
import com.example.taskman.ui.search.SearchViewModel
import com.example.taskman.ui.utils.HistoryRepository
import com.example.taskman.ui.utils.HistoryRepositoryImpl
import com.example.taskman.ui.utils.OptionViewModel
import com.example.taskman.ui.utils.SessionRepository
import com.example.taskman.ui.utils.SessionRepositoryImpl
import com.example.taskman.ui.utils.ThemeRepository
import com.example.taskman.ui.utils.ThemeRepositoryImpl
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val dataModule = module {
    single<RetrofitClient> { RetrofitClient.init(get<AuthStorage>()) }
    single<SharedPreferences> {
        get<Context>().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    single { get<RetrofitClient>().let { AuthClient.instance } }
    single { get<RetrofitClient>().let { ProfileClient.instance } }
    single { get<RetrofitClient>().let { SyncClient.instance } }

    single<ThemeRepository> { ThemeRepositoryImpl(get()) }
    single<HistoryRepository> { HistoryRepositoryImpl(get()) }

    single<TaskManDatabase> { TaskManDatabase.getInstance(get()) }
    single<AuthStorage> { AuthDataStore(get()) }
    single<SessionRepository> { SessionRepositoryImpl(get(), get()) }

    single<TaskDao> { get<TaskManDatabase>().taskDao() }
    single<GroupDao> { get<TaskManDatabase>().groupDao() }
}

val domainModule = module {
    single<AuthService> { AuthService(get(), get<SessionRepository>()) }
    single<ProfileService> { ProfileService(get()) }

    single<SyncService> { SyncService(get()) }
    single<WorkManager> { WorkManager.getInstance(get()) }
    single<SyncRepository> { SyncRepository(get(), get(), get()) }
}

val presentationModule = module {
    viewModel { OptionViewModel(get<ThemeRepository>()) }
    viewModel { AuthViewModel(get<AuthService>(), get(), get(), get()) }
    viewModel { ProfileViewModel(get<ProfileService>(), get<SessionRepository>()) }
    viewModel { MainViewModel(get<TaskDao>(), get<GroupDao>()) }
    viewModel { TaskControlViewModel(get<TaskDao>()) }
    viewModel { GroupControlViewModel(get<GroupDao>()) }
    viewModel { SearchViewModel(get<TaskDao>(), get<HistoryRepository>()) }
}