package com.example.taskman.di

import com.example.taskman.ui.auth.AuthService
import org.koin.dsl.module

val domainModule = module {
    single<AuthService> { AuthService(get()) }
}