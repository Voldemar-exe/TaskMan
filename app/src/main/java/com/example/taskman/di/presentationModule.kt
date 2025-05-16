package com.example.taskman.di

import com.example.taskman.api.auth.AuthService
import com.example.taskman.api.group.GroupService
import com.example.taskman.api.profile.ProfileService
import com.example.taskman.api.task.TaskService
import com.example.taskman.db.GroupDao
import com.example.taskman.db.TaskDao
import com.example.taskman.ui.auth.AuthViewModel
import com.example.taskman.ui.control.group.GroupControlViewModel
import com.example.taskman.ui.control.task.TaskControlViewModel
import com.example.taskman.ui.main.MainViewModel
import com.example.taskman.ui.profile.ProfileViewModel
import com.example.taskman.ui.search.SearchViewModel
import com.example.taskman.ui.utils.HistoryRepository
import com.example.taskman.ui.utils.OptionViewModel
import com.example.taskman.ui.utils.SessionRepository
import com.example.taskman.ui.utils.ThemeRepository
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { OptionViewModel(get<ThemeRepository>()) }
    viewModel { AuthViewModel(get<AuthService>(), get(), get(), get()) }
    viewModel { ProfileViewModel(get<ProfileService>(), get<SessionRepository>()) }
    viewModel { MainViewModel(get<TaskDao>(), get<GroupDao>()) }
    viewModel { TaskControlViewModel(get<TaskDao>(), get<TaskService>()) }
    viewModel { GroupControlViewModel(get<GroupDao>(), get<GroupService>()) }
    viewModel { SearchViewModel(get<TaskDao>(), get<HistoryRepository>()) }
}