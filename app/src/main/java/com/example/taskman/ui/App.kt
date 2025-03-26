package com.example.taskman.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.taskman.ui.auth.AuthenticationScreen
import com.example.taskman.ui.auth.ProfileScreen
import com.example.taskman.ui.main.TaskScreen
import kotlinx.serialization.Serializable


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    Scaffold { paddingValues ->
        NavHost(
            modifier = modifier
                .padding(paddingValues),
            navController = navController,
            startDestination = Main
        ) {
            composable<Profile> { backStackEntry ->
                val profile: Profile = backStackEntry.toRoute()
                ProfileScreen(
                    userName = profile.name!!,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
            composable<Main> {
                TaskScreen(
                    onProfileClick = {
                        val profile = Profile(name = "ME")
                        if (profile.name == null) {
                            navController.navigate(Authentication("login"))
                        } else {
                            navController.navigate(route = profile)
                        }
                    }
                )
            }
            composable<Authentication> { backStackEntry ->
                val authentication: Authentication = backStackEntry.toRoute()

                AuthenticationScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }


            composable<SearchScreen> {
                SearchScreen()
            }
        }
    }
}

@Serializable
data class Profile(val name: String?)
@Serializable
object Main
@Serializable
data class Authentication(val type: String)
@Serializable
object SearchScreen