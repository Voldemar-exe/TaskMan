package com.example.taskman.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun App(
    isActiveSession: Boolean,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    Scaffold { paddingValues ->
        NavHost(
            modifier = modifier
                .padding(paddingValues),
            navController = navController,
            startDestination = Splash
        ) {
            splashGraph(navController)
            profileGraph(navController)
            homeGraph(isActiveSession, navController)
            authGraph(navController)
            searchGraph(navController)
            taskControlGraph(navController)
            groupControlGraph(navController)
            settingsGraph(navController)
        }
    }
}
