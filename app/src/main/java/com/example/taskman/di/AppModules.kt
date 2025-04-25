package com.example.taskman.di

import com.example.taskman.db.GroupDao
import com.example.taskman.db.TaskDao
import com.example.taskman.db.TaskManDatabase
import com.example.taskman.ui.auth.AuthClient
import com.example.taskman.ui.auth.AuthService
import com.example.taskman.ui.auth.AuthViewModel
import com.example.taskman.ui.auth.ProfileViewModel
import com.example.taskman.ui.group.GroupControlViewModel
import com.example.taskman.ui.main.MainViewModel
import com.example.taskman.ui.task.TaskControlViewModel
import com.example.taskman.ui.utils.OptionViewModel
import com.example.taskman.ui.utils.ThemeRepository
import com.example.taskman.ui.utils.ThemeRepositoryImpl
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<ThemeRepository> { ThemeRepositoryImpl(get()) }
    single<TaskManDatabase> { TaskManDatabase.getInstance(get()) }
    single<TaskDao> { get<TaskManDatabase>().taskDao() }
    single<GroupDao> { get<TaskManDatabase>().groupDao() }
    single<HttpClient> {
        HttpClient {
            install(ContentNegotiation) {
                json()
            }
        }
    }
    single<AuthService> { AuthService(AuthClient.instance) }

    viewModel { OptionViewModel(get()) }

    viewModel { AuthViewModel(get()) }
    viewModel { ProfileViewModel() }

    viewModel { MainViewModel(get<TaskDao>(), get<GroupDao>()) }
    viewModel { TaskControlViewModel(get<TaskDao>()) }
    viewModel { GroupControlViewModel(get()) }
}