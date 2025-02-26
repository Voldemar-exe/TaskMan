package com.example.taskman.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable


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
                        val profile = Profile("Я")
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
                    isRegister = authentication.type == "register",
                    title = if (authentication.type == "login") "Вход" else "Регистрация",
                    onRegistrationClick = {
                        navController.navigate(route = Authentication("register"))
                    },
                    onAction = {},
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
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