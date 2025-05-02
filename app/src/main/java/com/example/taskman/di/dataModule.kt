package com.example.taskman.di

import com.example.taskman.api.auth.AuthClient
import com.example.taskman.api.task.TaskClient
import com.example.taskman.db.GroupDao
import com.example.taskman.db.TaskDao
import com.example.taskman.db.TaskManDatabase
import com.example.taskman.ui.utils.ThemeRepository
import com.example.taskman.ui.utils.ThemeRepositoryImpl
import org.koin.dsl.module

val dataModule = module {
    single { AuthClient.instance }
    single { TaskClient.instance }
    single<ThemeRepository> { ThemeRepositoryImpl(get()) }
    single<TaskManDatabase> { TaskManDatabase.getInstance(get()) }
    single<TaskDao> { get<TaskManDatabase>().taskDao() }
    single<GroupDao> { get<TaskManDatabase>().groupDao() }
}