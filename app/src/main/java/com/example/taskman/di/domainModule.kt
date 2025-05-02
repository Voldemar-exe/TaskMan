package com.example.taskman.di

import com.example.taskman.api.auth.AuthService
import com.example.taskman.api.task.TaskService
import org.koin.dsl.module

val domainModule = module {
    single<AuthService> { AuthService(get()) }
    single<TaskService> { TaskService(get()) }
}