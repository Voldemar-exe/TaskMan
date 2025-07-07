package com.example.taskman

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.auth.ui.AuthScreen
import com.example.home.MainScreen
import com.example.profile.ProfileIntent
import com.example.profile.ProfileScreen
import com.example.profile.ProfileViewModel
import com.example.search.SearchScreen
import com.example.taskman.navigation.Authentication
import com.example.taskman.navigation.Main
import com.example.taskman.navigation.Profile
import com.example.taskman.navigation.SearchScreen
import com.example.taskman.navigation.Splash

@Composable
fun App(
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    isDarkTheme: Boolean,
    toggleTheme: () -> Unit,
    navController: NavHostController = rememberNavController()
) {

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

                SearchScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
