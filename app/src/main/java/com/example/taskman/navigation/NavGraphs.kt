package com.example.taskman.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.auth.ui.AuthScreen
import com.example.control.group.GroupControl
import com.example.control.task.TaskControl
import com.example.home.MainScreen
import com.example.profile.ProfileScreen
import com.example.search.SearchScreen
import com.example.settings.SettingsScreen
import com.example.taskman.SplashScreen

fun NavGraphBuilder.splashGraph(navController: NavController) {
    composable<Splash> {
        SplashScreen(
            onSplashFinished = {
                navController.navigate(Main) {
                    popUpTo(Splash) { inclusive = true }
                }
            }
        )
    }
}

fun NavGraphBuilder.profileGraph(navController: NavController) {
    composable<Profile> {
        ProfileScreen(
            onBackClick = { navController.navigate(Main) }
        )
    }
}

fun NavGraphBuilder.mainGraph(
    isActiveSession: Boolean,
    navController: NavController
) {
    composable<Main> {
        MainScreen(
            onProfileClick = {
                if (isActiveSession) {
                    navController.navigate(Profile)
                } else {
                    navController.navigate(Authentication("login"))
                }
            },
            onTaskControlClick = {
                navController.navigate(TaskControl(it))
            },
            onGroupControlClick = {
                navController.navigate(GroupControl(it))
            },
            onSearchClick = {
                navController.navigate(SearchScreen)
            }
        )
    }
}

fun NavGraphBuilder.authGraph(navController: NavController) {
    composable<Authentication> {
        AuthScreen(
            onBackClick = {
                navController.popBackStack()
            },
            loginUser = {
                navController.navigate(Profile)
            }
        )
    }
}

fun NavGraphBuilder.searchGraph(navController: NavController) {
    composable<SearchScreen> {
        SearchScreen(
            onBackClick = {
                navController.popBackStack()
            }
        )
    }
}

fun NavGraphBuilder.taskControlGraph(navController: NavController) {
    composable<TaskControl> { backStackEntry  ->
        val taskControl = backStackEntry.toRoute<TaskControl>()
        TaskControl(
            taskId = taskControl.taskId,
            onBackClick = { navController.popBackStack() }
        )
    }
}

fun NavGraphBuilder.groupControlGraph(navController: NavController) {
    composable<GroupControl> { backStackEntry  ->
        val groupControl = backStackEntry.toRoute<GroupControl>()
        GroupControl(
            groupId = groupControl.groupId,
            onBackClick = { navController.popBackStack() }
        )
    }
}

fun NavGraphBuilder.settingsGraph(navController: NavController) {
    composable<Settings> {
        SettingsScreen()
    }
}
