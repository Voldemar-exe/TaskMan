package com.example.taskman.di

import com.example.taskman.api.auth.AuthService
import com.example.taskman.api.profile.ProfileService
import com.example.taskman.db.GroupDao
import com.example.taskman.db.TaskDao
import com.example.taskman.ui.auth.AuthViewModel
import com.example.taskman.ui.control.group.GroupControlViewModel
import com.example.taskman.ui.control.task.TaskControlViewModel
import com.example.taskman.ui.main.MainViewModel
import com.example.taskman.ui.profile.ProfileViewModel
import com.example.taskman.ui.search.SearchViewModel
import com.example.taskman.ui.utils.HistoryRepository
import com.example.taskman.ui.utils.SessionRepository
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { AuthViewModel(get<AuthService>(), get(), get(), get()) }
    viewModel { ProfileViewModel(get<ProfileService>(), get<SessionRepository>()) }
    viewModel { MainViewModel(get<TaskDao>(), get<GroupDao>()) }
    viewModel { TaskControlViewModel(get<TaskDao>()) }
    viewModel { GroupControlViewModel(get<GroupDao>()) }
    viewModel { SearchViewModel(get<TaskDao>(), get<HistoryRepository>()) }
}