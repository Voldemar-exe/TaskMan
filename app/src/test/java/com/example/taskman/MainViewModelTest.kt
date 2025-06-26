package com.example.taskman

import com.example.taskman.db.GroupDao
import com.example.taskman.db.TaskDao
import com.example.taskman.model.MyTask
import com.example.taskman.ui.main.MainIntent
import com.example.taskman.ui.main.MainState
import com.example.taskman.ui.main.MainViewModel
import io.mockk.*
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MainViewModelTest {

    private lateinit var viewModel: MainViewModel
    private val mockTaskDao = mockk<TaskDao>()
    private val mockGroupDao = mockk<GroupDao>()

    @Before
    fun setUp() {
        val taskId = 1
        val initialTask = MyTask(
            taskId = taskId,
            name = "Test Task",
            icon = "ic_cake",
            color = 0xFFFFFFFF,
            type = "personal",
            isComplete = false,
            date = System.currentTimeMillis()
        )
        viewModel = MainViewModel(
            taskDao = mockTaskDao,
            groupDao = mockGroupDao,
            initialState = MainState(visibleTasks = listOf(initialTask))
        )
    }

    @Test
    fun `when ToggleTaskCompletion intent is sent, task should toggle completion status`() =
        runBlocking {}
}