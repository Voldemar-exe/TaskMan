package com.example.taskman.navigation

import androidx.navigation.NavHostController

class AppNavigator(private val navController: NavHostController) {

    fun navigateToProfile() {
        navController.navigate(Profile)
    }

    fun navigateToAuth(mode: String) {
        navController.navigate(Authentication(mode))
    }

    fun goBack() {
        navController.popBackStack()
    }
}