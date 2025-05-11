package com.example.di

import com.example.auth.AuthService
import com.example.auth.AuthServiceImpl
import com.example.db.repository.GroupRepository
import com.example.db.repository.GroupRepositoryImpl
import com.example.db.repository.TaskRepository
import com.example.db.repository.TaskRepositoryImpl
import com.example.db.repository.TokenRepository
import com.example.db.repository.TokenRepositoryImpl
import com.example.db.repository.UserRepository
import com.example.db.repository.UserRepositoryImpl
import org.koin.dsl.module

val authModule = module {
    single<TokenRepository> { TokenRepositoryImpl() }
    single<UserRepository> { UserRepositoryImpl() }
    single<TaskRepository> { TaskRepositoryImpl() }
    single<GroupRepository> { GroupRepositoryImpl() }
    single<AuthService> { AuthServiceImpl(get(), get(), get(), get()) }
}