package com.example.taskman.di

import com.example.taskman.ui.auth.AuthService
import com.example.taskman.ui.auth.AuthViewModel
import com.example.taskman.ui.auth.ProfileViewModel
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
    single<HttpClient> {
        HttpClient {
            install(ContentNegotiation) {
                json()
            }
        }
    }
    single<AuthService> { AuthService() }
    viewModel { AuthViewModel(get()) }
    viewModel { OptionViewModel(get()) }
    viewModel { ProfileViewModel() }
}