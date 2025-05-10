package com.example.di

import com.example.auth.AuthService
import com.example.auth.AuthServiceImpl
import com.example.db.TokenRepository
import com.example.db.TokenRepositoryImpl
import com.example.db.UserRepository
import com.example.db.UserRepositoryImpl
import org.koin.dsl.module

val authModule = module {
    single<TokenRepository> { TokenRepositoryImpl() }
    single<UserRepository> { UserRepositoryImpl() }
    single<AuthService> { AuthServiceImpl(get(), get()) }
}