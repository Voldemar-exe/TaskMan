package com.example.profile

import com.example.data.TokenProvider
import com.example.data.repository.SessionRepository
import com.example.network.retrofit.profile.ProfileApi
import com.example.network.retrofit.profile.ProfileService
import com.example.shared.ProfileData
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class ProfileViewModelTest {

    private lateinit var viewModel: ProfileViewModel

    private val mockTokenProvider = mockk<TokenProvider>()
    private val mockProfileApi = mockk<ProfileApi>()
    private val mockSessionRepository = mockk<SessionRepository>()
    private val testUserData =
        ProfileData("test_token", "test_user", "test@mail.com")
    private val mockProfileService = mockk<ProfileService>()

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        coEvery { mockTokenProvider.getToken() } returns "test_token"
        coEvery { mockSessionRepository.getProfileData() } returns testUserData
        coEvery { mockSessionRepository.saveSession(any()) } just Runs
        coEvery { mockSessionRepository.clearSession() } just Runs

        viewModel = ProfileViewModel(
            ProfileService(mockProfileApi),
            mockSessionRepository
        )
    }

    @Test
    fun `when LoadProfile intent then username is set`() = runTest {
        viewModel.onIntent(ProfileIntent.LoadProfile)

        assertEquals("test_user", viewModel.uiState.value.username)
    }

    @Test
    fun `when InfoClick intent then showInfoDialog is true`() = runTest {
        viewModel.onIntent(ProfileIntent.InfoClick)

        assertTrue(viewModel.uiState.value.showInfo)
    }

    @Test
    fun `when ClearProfile intent then clearSession is called`() = runTest {
        viewModel.onIntent(ProfileIntent.ClearProfile)

        coVerify { mockSessionRepository.clearSession() }

        assertTrue(viewModel.uiState.value.username.isEmpty())
        assertTrue(viewModel.uiState.value.email.isEmpty())
    }

    @Test
    fun `when ClearSuccess intent then success message is shown`() = runTest {
        viewModel.onIntent(ProfileIntent.ClearSuccess)

        assertFalse(viewModel.uiState.value.success)
    }

    @Test
    fun `when ClearError intent then error message is cleared`() = runTest {
        viewModel.onIntent(ProfileIntent.ClearError)

        assertTrue(viewModel.uiState.value.error == null)
    }

    // TODO RESOLVE HOW TO VERIFY FUNC IN viewmodelScope
    /*@Test
    fun `when DeleteProfile intent then deleteProfile is called`() = runTest {
        coEvery { mockProfileService.deleteUser() } returns true

        viewModel.onIntent(ProfileIntent.DeleteProfile)

        coVerify { mockProfileService.deleteUser() }
        coVerify { mockSessionRepository.clearSession() }

        assertTrue(viewModel.uiState.value.success)
    }

    @Test
    fun `when DeleteProfileData intent then clearDatabaseData is called`() = runTest {
        viewModel.onIntent(ProfileIntent.DeleteProfileData)

        coVerify { mockSessionRepository.clearDatabaseData() }

        assertTrue(viewModel.uiState.value.success)
    }*/
}