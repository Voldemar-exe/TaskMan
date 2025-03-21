package com.example.taskman

import com.example.taskman.ui.auth.AuthService
import com.example.taskman.ui.auth.AuthViewModel
import com.example.taskman.ui.auth.ProfileViewModel
import com.example.taskman.ui.utils.ThemeRepository
import com.example.taskman.ui.utils.ThemeRepositoryImpl
import com.example.taskman.ui.utils.ThemeViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<ThemeRepository> { ThemeRepositoryImpl(get()) }
    single<HttpClient> {
        HttpClient {
            install(ContentNegotiation) {
                json()
            }
        }
    }
    single<AuthService> { AuthService(get()) }
    viewModel { AuthViewModel(get<AuthService>()) }
    viewModel { ThemeViewModel(get()) }
    viewModel { ProfileViewModel() }
}