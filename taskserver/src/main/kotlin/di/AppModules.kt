package com.example.di

import com.example.auth.AuthService
import com.example.auth.AuthServiceImpl
import com.example.auth.TokenRepositoryImpl
import com.example.auth.UserRepositoryImpl
import com.example.db.TokenRepository
import com.example.db.UserRepository
import org.koin.dsl.module

val authModule = module {
    single<TokenRepository> { TokenRepositoryImpl() }
    single<UserRepository> { UserRepositoryImpl() }
    single<AuthService> { AuthServiceImpl(get(), get()) }
}