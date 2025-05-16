package com.example.taskman.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.taskman.api.auth.ProfileData
import com.example.taskman.navigation.Authentication
import com.example.taskman.navigation.Main
import com.example.taskman.navigation.Profile
import com.example.taskman.navigation.SearchScreen
import com.example.taskman.navigation.Splash
import com.example.taskman.ui.auth.AuthStorage
import com.example.taskman.ui.auth.AuthenticationScreen
import com.example.taskman.ui.control.group.GroupControlViewModel
import com.example.taskman.ui.control.task.TaskControlViewModel
import com.example.taskman.ui.main.MainIntent
import com.example.taskman.ui.main.MainScreen
import com.example.taskman.ui.main.MainViewModel
import com.example.taskman.ui.profile.ProfileScreen
import com.example.taskman.ui.search.SearchScreen
import com.example.taskman.ui.search.SearchViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject


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
            startDestination = Splash
        ) {

            composable<Splash> {
                SplashScreen {
                    navController.navigate(Main) {
                        popUpTo(Splash) { inclusive = true }
                    }
                }
            }

            composable<Profile> { backStackEntry ->
                val profile: Profile = backStackEntry.toRoute<Profile>()
                ProfileScreen(
                    userName = profile.name,
                    onBackClick = {
                        navController.navigate(Main)
                    }
                )
            }
            composable<Main> {
                val taskControlViewModel = koinViewModel<TaskControlViewModel>()
                val groupControlViewModel = koinViewModel<GroupControlViewModel>()
                val authStorage = koinInject<AuthStorage>()

                val profile by produceState<ProfileData?>(initialValue = null) {
                    value = authStorage.getProfile()
                }

                MainScreen(
                    mainViewModel = mainViewModel,
                    taskControlViewModel = taskControlViewModel,
                    groupControlViewModel = groupControlViewModel,
                    onProfileClick = {
                        val route = profile?.let { user ->
                            val displayName = user.username
                                ?.takeUnless { it.isBlank() }
                                ?: user.login
                            Profile(displayName)
                        } ?: Authentication("login")

                        navController.navigate(route)
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
                val uiState by searchViewModel.state.collectAsStateWithLifecycle()
                val history by searchViewModel.history.collectAsStateWithLifecycle()
                val allTasks by mainViewModel.allTasks.collectAsStateWithLifecycle()
                SearchScreen(
                    allTasks = allTasks,
                    history = history,
                    state = uiState,
                    onIntent = searchViewModel::onIntent,
                    onTaskCheckClick = { task ->
                        mainViewModel.onIntent(
                            MainIntent.ToggleTaskCompletion(task)
                        )
                    }
                )
            }
        }
    }
}
