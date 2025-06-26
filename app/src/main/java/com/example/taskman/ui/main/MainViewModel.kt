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
        viewModelScope.launch(Dispatchers.IO) {
            combine(
                _mainState.map { it.selectedGroupId }.distinctUntilChanged(),
                _mainState.map { it.selectedTaskTypes }.distinctUntilChanged(),
                _mainState.map { it.selectedTabIndex }.distinctUntilChanged(),
                allTasks,
                allGroupsWithTasks
            ) { groupId, taskTypes, tabIndex, tasks, groupsWithTasks ->
                filterVisibleTasks(tasks, groupId, taskTypes, tabIndex, groupsWithTasks)
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

            is MainIntent.SelectGroup ->
                _mainState.update {
                    it.copy(
                        selectedGroupId = intent.group.groupId,
                        selectedGroupName = intent.group.name
                    )
                }

            is MainIntent.SelectTaskType -> {
                _mainState.update { state ->
                    state.copy(
                        selectedTaskTypes = if (intent.taskType in state.selectedTaskTypes) {
                            state.selectedTaskTypes - intent.taskType
                        } else {
                            state.selectedTaskTypes + intent.taskType
                        }
                    )
                }
            }

            is MainIntent.LoadTasks -> Unit
            is MainIntent.SelectTab ->
                _mainState.update { it.copy(selectedTabIndex = intent.tabIndex) }
            MainIntent.SyncData -> viewModelScope.launch {
                taskDao.syncAllTasks()
                groupDao.syncAllGroups()
            }
        }
    }

    private fun toggleTaskCompletion(task: MyTask) = viewModelScope.launch {
        taskDao.updateTask(task.copy(isComplete = !task.isComplete))
    }

    private fun filterVisibleTasks(
        allTasks: List<MyTask>,
        selectedGroupId: Int,
        selectedTaskTypes: Set<TaskType>,
        selectedTabIndex: Int,
        groupsWithTasks: List<GroupWithTasks>
    ): List<MyTask> {
        val filteredByGroup = when (selectedGroupId) {
            -1 -> allTasks
            else -> {
                groupsWithTasks.find { it.group.groupId == selectedGroupId }?.tasks ?: emptyList()
            }
        }

        val filteredByTab = when (selectedTabIndex) {
            1 -> filteredByGroup.filterNot { it.isComplete }
            2 -> filteredByGroup.filter { it.isComplete }
            else -> filteredByGroup
        }

        val filteredByType = if (selectedTaskTypes.isNotEmpty()) {
            val allowedTypes = selectedTaskTypes.map { it.name }
            filteredByTab.filter { it.type in allowedTypes }
        } else {
            filteredByTab
        }

        return filteredByType.sortedByDescending { it.taskId }
    }
}
