package com.example.taskman.ui.main

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskman.db.GroupDao
import com.example.taskman.db.GroupWithTasks
import com.example.taskman.db.TaskDao
import com.example.taskman.model.MyTask
import com.example.taskman.model.TaskGroup
import com.example.taskman.model.TaskType
import com.example.taskman.ui.main.sheet.MainBottomSheetType
import com.example.taskman.ui.utils.ItemIcon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val taskDao: TaskDao,
    private val groupDao: GroupDao,
    initialState: MainState = MainState()
) : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
        private const val STOP_DELAY = 5000L
    }

    private val _mainState = MutableStateFlow(initialState)
    val mainState = _mainState.asStateFlow()

    val allTasks = taskDao.getAllTasksFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_DELAY),
        emptyList()
    )

    private val allGroupsWithTasks = groupDao.getAllGroupsWithTasksFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_DELAY),
        emptyList()
    )

    val allGroups = groupDao.getAllGroupsFlow().map {
        listOf(
            TaskGroup(
                groupId = -1,
                serverId = null,
                name = "Все",
                icon = ItemIcon.Amount.name,
                color = Color.Black.toArgb().toLong()
            )
        ) + it
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_DELAY),
        emptyList()
    )

    init {
        Log.i(TAG, "init: $TAG}")
        viewModelScope.launch(Dispatchers.IO) {
            combine(
                _mainState.map { it.selectedGroupId }.distinctUntilChanged(),
                _mainState.map { it.selectedTaskTypes }.distinctUntilChanged(),
                _mainState.map { it.selectedTabIndex }.distinctUntilChanged(),
                allTasks,
                allGroupsWithTasks
            ) { selectedGroupId, selectedTaskTypes, selectedTabIndex, tasks, groupsWithTasks ->
                val tasksByGroup = getTasksByGroupOrNull(selectedGroupId, groupsWithTasks).let {
                    tasks
                }
                val filteredByTab = filterTasksByTab(selectedTabIndex, tasksByGroup)
                val filteredByTypes = filterTasksByType(selectedTaskTypes, filteredByTab)
                filteredByTypes.sortedByDescending { it.taskId }
            }.collect { filteredTasks ->
                _mainState.update { it.copy(visibleTasks = filteredTasks) }
            }
        }
    }

    fun onIntent(intent: MainIntent) {
        Log.i(TAG, "$intent")
        when (intent) {
            is MainIntent.ToggleTaskCompletion -> toggleTaskCompletion(intent.task)
            is MainIntent.ShowBottomSheet ->
                _mainState.update { it.copy(bottomSheet = intent.type) }
            MainIntent.CloseBottomSheet ->
                _mainState.update { it.copy(bottomSheet = MainBottomSheetType.None) }
            is MainIntent.SelectTask ->
                _mainState.update { it.copy(selectedTaskId = intent.taskId) }
            is MainIntent.SelectGroup -> selectGroup(intent.group)
            is MainIntent.SelectTaskType -> selectTaskType(intent.taskType)
            is MainIntent.SelectTab ->
                _mainState.update { it.copy(selectedTabIndex = intent.tabIndex) }
            MainIntent.SyncData -> syncAllTasksAndGroups()
        }
    }

    private fun getTasksByGroupOrNull(
        selectedGroupId: Int,
        groupsWithTasks: List<GroupWithTasks>
    ): List<MyTask>? {
        return groupsWithTasks.find { it.group.groupId == selectedGroupId }?.tasks
    }

    private fun filterTasksByTab(
        selectedTabIndex: Int,
        tasks: List<MyTask>
    ) = when (selectedTabIndex) {
        1 -> tasks.filterNot { it.isComplete }
        2 -> tasks.filter { it.isComplete }
        else -> tasks
    }

    private fun filterTasksByType(
        selectedTaskTypes: Set<TaskType>,
        tasks: List<MyTask>
    ) = if (selectedTaskTypes.isNotEmpty()) {
        val allowedTypes = selectedTaskTypes.map { it.name }
        tasks.filter { it.type in allowedTypes }
    } else {
        tasks
    }

    private fun toggleTaskCompletion(task: MyTask) = viewModelScope.launch {
        taskDao.updateTask(task.copy(isComplete = !task.isComplete))
    }

    private fun selectGroup(group: TaskGroup) {
        _mainState.update {
            it.copy(
                selectedGroupId = group.groupId,
                selectedGroupName = group.name
            )
        }
    }

    private fun selectTaskType(taskType: TaskType) {
        _mainState.update { state ->
            state.copy(
                selectedTaskTypes = if (taskType in state.selectedTaskTypes) {
                    state.selectedTaskTypes - taskType
                } else {
                    state.selectedTaskTypes + taskType
                }
            )
        }
    }

    private fun syncAllTasksAndGroups() = viewModelScope.launch {
        taskDao.syncAllTasks()
        groupDao.syncAllGroups()
    }
}
