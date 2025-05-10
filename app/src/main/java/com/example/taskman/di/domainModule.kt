package com.example.taskman.di

import com.example.taskman.api.auth.AuthService
import com.example.taskman.api.group.GroupService
import com.example.taskman.api.task.TaskService
import com.example.taskman.ui.auth.AuthStorage
import org.koin.dsl.module

val domainModule = module {
    single<AuthService> { AuthService(get(), get<AuthStorage>()) }
    single<TaskService> { TaskService(get()) }
    single<GroupService> { GroupService(get()) }
}