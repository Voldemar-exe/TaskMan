package com.example.taskman.di

import com.example.taskman.ui.auth.AuthDataStore
import com.example.taskman.ui.auth.AuthStorage
import org.koin.dsl.module

val networkModule = module {
    single<AuthStorage> { AuthDataStore(get()) }
}