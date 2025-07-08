package com.example.taskman

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.taskman.navigation.Splash
import com.example.taskman.navigation.authGraph
import com.example.taskman.navigation.groupControlGraph
import com.example.taskman.navigation.mainGraph
import com.example.taskman.navigation.profileGraph
import com.example.taskman.navigation.searchGraph
import com.example.taskman.navigation.splashGraph
import com.example.taskman.navigation.taskControlGraph

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
            mainGraph(isActiveSession, navController)
            authGraph(navController)
            searchGraph(navController)
            taskControlGraph(navController)
            groupControlGraph(navController)
        }
    }
}
