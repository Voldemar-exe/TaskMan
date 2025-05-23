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
import com.example.taskman.navigation.Authentication
import com.example.taskman.navigation.Main
import com.example.taskman.navigation.Profile
import com.example.taskman.navigation.SearchScreen
import com.example.taskman.navigation.Splash
import com.example.taskman.ui.auth.AuthScreen
import com.example.taskman.ui.main.MainIntent
import com.example.taskman.ui.main.MainScreen
import com.example.taskman.ui.main.MainViewModel
import com.example.taskman.ui.profile.ProfileIntent
import com.example.taskman.ui.profile.ProfileScreen
import com.example.taskman.ui.profile.ProfileViewModel
import com.example.taskman.ui.search.SearchScreen
import com.example.taskman.ui.search.SearchViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    toggleTheme: () -> Unit,
    navController: NavHostController = rememberNavController()
) {
    val mainViewModel = koinViewModel<MainViewModel>()
    val profileViewModel = koinViewModel<ProfileViewModel>()

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
                val profileState by profileViewModel.uiState.collectAsStateWithLifecycle()

                ProfileScreen(
                    isDarkTheme = isDarkTheme,
                    toggleTheme = toggleTheme,
                    state = profileState,
                    onIntent = profileViewModel::onIntent,
                    onBackClick = { navController.navigate(Main) }
                )
            }
            composable<Main> {

                MainScreen(
                    mainViewModel = mainViewModel,
                    onProfileClick = {
                        val route = if (profileViewModel.uiState.value.username.isNotEmpty()) {
                            Profile
                        } else {
                            Authentication("login")
                        }
                        navController.navigate(route)
                    },
                    onSearchClick = {
                        navController.navigate(SearchScreen)
                    }
                )
            }
            composable<Authentication> {
                AuthScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    loginUser = {
                        profileViewModel.onIntent(ProfileIntent.LoadProfile)
                        navController.navigate(it)
                    }
                )
            }
            composable<SearchScreen> {
                val searchViewModel = koinViewModel<SearchViewModel>()
                val uiState by searchViewModel.state.collectAsStateWithLifecycle()
                val history by searchViewModel.history.collectAsStateWithLifecycle()

                SearchScreen(
                    history = history,
                    state = uiState,
                    onIntent = searchViewModel::onIntent,
                    onTaskCheckClick = { task ->
                        mainViewModel.onIntent(
                            MainIntent.ToggleTaskCompletion(task)
                        )
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
