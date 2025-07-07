package com.example.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.GroupRepository
import com.example.data.repository.TaskRepository
import com.example.home.sheet.MainBottomSheetType
import com.example.shared.TaskType
import com.example.shared.UserGroupWithTasks
import com.example.shared.UserTask
import com.example.shared.UserTaskGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
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

@HiltViewModel
class MainViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val groupRepository: GroupRepository
) : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
        private const val STOP_DELAY = 5000L
    }

    private val _mainState = MutableStateFlow(MainState())
    val mainState = _mainState.asStateFlow()

    val allTasks = taskRepository.tasksListFlow
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STOP_DELAY),
            emptyList()
        )

    private val allGroupsWithTasks = groupRepository.allGroupsWithTasksFlow
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STOP_DELAY),
            emptyList()
        )

    val allGroups = groupRepository.allGroupsFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_DELAY),
        emptyList()
    )

    init {
        Log.i(TAG, "init: $TAG")
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
                filteredByTypes.sortedByDescending { it.localId }
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
        groupsWithTasks: List<UserGroupWithTasks>
    ): List<UserTask>? {
        return groupsWithTasks.find { it.group.localId == selectedGroupId }?.tasks
    }

    private fun filterTasksByTab(
        selectedTabIndex: Int,
        tasks: List<UserTask>
    ) = when (selectedTabIndex) {
        1 -> tasks.filterNot { it.isComplete }
        2 -> tasks.filter { it.isComplete }
        else -> tasks
    }

    private fun filterTasksByType(
        selectedTaskTypes: Set<TaskType>,
        tasks: List<UserTask>
    ) = if (selectedTaskTypes.isNotEmpty()) {
        val allowedTypes = selectedTaskTypes.map { it.name }
        tasks.filter { it.type in allowedTypes }
    } else {
        tasks
    }

    private fun toggleTaskCompletion(task: UserTask) = viewModelScope.launch {
        taskRepository.toggleTaskCompletion(task)
    }

    private fun selectGroup(group: UserTaskGroup) {
        _mainState.update {
            it.copy(
                selectedGroupId = group.localId,
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
        taskRepository.syncAllTasks()
        groupRepository.syncAllGroups()
    }
}
