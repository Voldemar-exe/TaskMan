package com.example.taskman

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
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
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RealServerAuthTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val testUser = "testuser_${System.currentTimeMillis()}"
    private val testPassword = "TestPass123!"
    private val testUserName = "Test"

    private val navController = TestNavHostController(
        ApplicationProvider.getApplicationContext()
    )

    @Test
    fun realRegistrationAndLogin() {
        // Arrange
        composeTestRule.setContent {
            AuthenticationScreen(
                viewModel = AuthViewModel(AuthService()),
                onBackClick = { navController.popBackStack() },
                loginUser = { profile ->
                    navController.navigate(profile)
                }
            )
        }

        composeTestRule.onNodeWithText("Зарегистрироваться").performClick()

        composeTestRule.onNodeWithText("Введите логин")
            .performTextInput(testUser)
        composeTestRule.onNodeWithText("Введите пароль")
            .performTextInput(testPassword)
        composeTestRule.onNodeWithText("Ещё раз введите пароль")
            .performTextInput(testPassword)

        composeTestRule.onNodeWithText("Регистрация").performClick()

        // Assert - Проверка перехода в профиль
        composeTestRule.waitUntil(10_000) {
            navController.currentDestination?.equals(Profile) == true
        }

        /*// Дополнительная проверка через API
        val api = AuthClient.authApi
        val user = api.getUser(testUser).execute().body()
        assertNotNull(user)
        assertEquals(testUser, user?.login)*/
    }

    @After
    fun tearDown() {
        TODO("uncomment to delete")
//        AuthService.deleteUser(testUser).execute()
    }
}