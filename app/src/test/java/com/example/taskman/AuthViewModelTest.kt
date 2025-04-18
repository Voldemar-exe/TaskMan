package com.example.taskman

import com.example.taskman.ui.auth.AuthIntent
import com.example.taskman.ui.auth.AuthService
import com.example.taskman.ui.auth.AuthViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel
    private val authService = mockk<AuthService>()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        viewModel = AuthViewModel(authService)
    }

    @Test
    fun `Registration success`() = runTest {
        // Arrange
        coEvery { authService.registerUser(any()) } returns "success"
        viewModel.processIntent(AuthIntent.UpdateLogin("user"))
        viewModel.processIntent(AuthIntent.UpdatePassword("pass"))
        viewModel.processIntent(AuthIntent.UpdateConfirmPassword("pass"))
        viewModel.processIntent(AuthIntent.ToggleMode)

        // Act
        viewModel.processIntent(AuthIntent.Submit)

        // Assert
        assertTrue(viewModel.uiState.value.success == true)
    }

    @Test
    fun `Password mismatch shows error`() {
        // Arrange
        viewModel.processIntent(AuthIntent.UpdatePassword("pass1"))
        viewModel.processIntent(AuthIntent.UpdateConfirmPassword("pass2"))
        viewModel.processIntent(AuthIntent.ToggleMode)

        // Act
        viewModel.processIntent(AuthIntent.Submit)

        // Assert
        assertEquals("Пароли не совпадают", viewModel.uiState.value.error)
    }
}