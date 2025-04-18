package com.example.taskman

import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.taskman.ui.Profile
import com.example.taskman.ui.auth.AuthService
import com.example.taskman.ui.auth.AuthViewModel
import com.example.taskman.ui.auth.AuthenticationScreen
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class AuthenticationScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val navController = TestNavHostController(
        ApplicationProvider.getApplicationContext()
    )

    @Test
    fun registrationFlow() {
        // Setup
        composeTestRule.setContent {
            val (loginUser, onLoginUser) = remember { mutableStateOf<Profile?>(null) }
            AuthenticationScreen(
                viewModel = AuthViewModel(AuthService()),
                onBackClick = { },
                loginUser = { onLoginUser(it) }
            )
        }

        // Act
        composeTestRule.onNodeWithText("Зарегистрироваться").performClick()
        composeTestRule.onNodeWithText("Введите логин").performTextInput("testuser")
        composeTestRule.onNodeWithText("Введите пароль").performTextInput("password")
        composeTestRule.onNodeWithText("Ещё раз введите пароль").performTextInput("password")
        composeTestRule.onNodeWithText("Регистрация").performClick()

        // Assert
        composeTestRule.waitUntil(20000) {
            navController.currentDestination?.route == "Profile"
        }
    }

    @Test
    fun navigationBackFromLogin() {
        // Setup
        composeTestRule.setContent {
            AuthenticationScreen(
                viewModel = AuthViewModel(AuthService()),
                onBackClick = { navController.popBackStack() },
                loginUser = {}
            )
        }

        // Act
        composeTestRule.onNodeWithContentDescription("Назад").performClick()

        // Assert
        assertTrue(navController.previousBackStackEntry?.destination?.route == "Main")
    }
}