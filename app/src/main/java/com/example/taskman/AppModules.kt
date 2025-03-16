package com.example.taskman

import com.example.taskman.ui.auth.ProfileViewModel
import com.example.taskman.ui.utils.ThemeRepository
import com.example.taskman.ui.utils.ThemeRepositoryImpl
import com.example.taskman.ui.utils.ThemeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<ThemeRepository> { ThemeRepositoryImpl(get()) }
    viewModel { ThemeViewModel(get()) }
    viewModel { ProfileViewModel() }
}