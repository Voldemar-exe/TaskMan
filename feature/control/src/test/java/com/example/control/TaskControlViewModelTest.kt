package com.example.control

import com.example.control.task.TaskControlViewModel
import com.example.data.TokenProvider
import com.example.data.repository.TaskRepository
import com.example.shared.ItemColor
import com.example.shared.SharedTask
import com.example.shared.TaskType
import com.example.shared.UserIcon
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class TaskControlViewModelTest {
    private lateinit var viewModel: TaskControlViewModel

    private val mockTokenProvider = mockk<TokenProvider>()
    private val mockTaskRepository = mockk<TaskRepository>()

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        coEvery { mockTokenProvider.getToken() } returns "test_token"
        coEvery { mockTaskRepository.insertWithSyncFlag(any()) } just Runs
        coEvery { mockTaskRepository.updateWithSyncFlag(any()) } just Runs

        viewModel = TaskControlViewModel(
            taskRepository = mockTaskRepository
        )
    }

    @Test
    fun `when UpdateType intent then type is updated`() = runTest {
        val newType = TaskType.Work
        viewModel.onIntent(TaskControlIntent.UpdateType(newType))
        assertEquals(newType, viewModel.uiState.value.task?.selectedType)
    }

    @Test
    fun `when UpdateDate intent then date is updated`() = runTest {
        val newDate = System.currentTimeMillis()
        viewModel.onIntent(TaskControlIntent.UpdateDate(newDate))
        assertEquals(newDate, viewModel.uiState.value.task?.selectedDate)
    }

    @Test
    fun `when LoadEntity intent then state is updated with loaded data`() = runTest {
        val entityId = 123
        val loadedTask = SharedTask(
                localId = entityId,
                name = "Loaded Task",
                date = 1690000000L,
                serverId = null,
                icon = UserIcon.Work,
                color = ItemColor.Red,
                type = TaskType.Work.name,
                isComplete = false
            )

        coEvery { mockTaskRepository.getTaskById(entityId) } returns loadedTask

        viewModel.onIntent(ControlIntent.LoadEntity(entityId))

        coVerify { mockTaskRepository.getTaskById(entityId) }

        val state = viewModel.uiState.value
        assertEquals(loadedTask.localId, state.base.entityId)
        assertEquals(loadedTask.name, state.base.entityName)
    }

}