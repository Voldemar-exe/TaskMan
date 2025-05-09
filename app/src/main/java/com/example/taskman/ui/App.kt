package com.example.taskman.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.taskman.ui.auth.AuthenticationScreen
import com.example.taskman.ui.auth.ProfileScreen
import com.example.taskman.ui.main.MainIntent
import com.example.taskman.ui.main.MainViewModel
import com.example.taskman.ui.main.TaskScreen
import com.example.taskman.ui.search.SearchScreen
import com.example.taskman.ui.search.SearchViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val mainViewModel = koinViewModel<MainViewModel>()

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
                        navController.navigate(Main)
                    }
                )
            }
            composable<Main> {
                TaskScreen(
                    mainViewModel = mainViewModel,
                    onProfileClick = {
                        val profile = Profile(name = null)
                        if (profile.name == null) {
                            navController.navigate(Authentication("login"))
                        } else {
                            navController.navigate(route = profile)
                        }
                    },
                    onSearchClick = {
                        navController.navigate(SearchScreen)
                    }
                )
            }
            composable<Authentication> { backStackEntry ->
                val authentication: Authentication = backStackEntry.toRoute()

                AuthenticationScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    loginUser = {
                        navController.navigate(it)
                    }
                )
            }
            composable<SearchScreen> {
                val searchViewModel = koinViewModel<SearchViewModel>()
                val uiState = searchViewModel.state.collectAsStateWithLifecycle()
                val allTasks by mainViewModel.allTasks.collectAsStateWithLifecycle()
                SearchScreen(
                    allTasks = allTasks,
                    state = uiState.value,
                    onIntent = searchViewModel::onIntent,
                    onTaskCheckClick = { task ->
                        mainViewModel.onIntent(
                            MainIntent.MainSwitch(task)
                        )
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
@Serializable
object SearchScreen