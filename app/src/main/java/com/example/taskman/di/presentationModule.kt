package com.example.taskman.di

import com.example.taskman.db.GroupDao
import com.example.taskman.db.TaskDao
import com.example.taskman.ui.auth.AuthViewModel
import com.example.taskman.ui.auth.ProfileViewModel
import com.example.taskman.ui.group.GroupControlViewModel
import com.example.taskman.ui.main.MainViewModel
import com.example.taskman.ui.search.SearchViewModel
import com.example.taskman.ui.task.TaskControlViewModel
import com.example.taskman.ui.utils.OptionViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { OptionViewModel(get()) }
    viewModel { AuthViewModel(get()) }
    viewModel { ProfileViewModel() }
    viewModel { MainViewModel(get<TaskDao>(), get<GroupDao>()) }
    viewModel { TaskControlViewModel(get()) }
    viewModel { GroupControlViewModel(get()) }
    viewModel { SearchViewModel(get()) }
}