package com.example.taskman.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskman.db.GroupDao
import com.example.taskman.db.TaskDao
import com.example.taskman.model.MyTask
import com.example.taskman.model.TaskGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val taskDao: TaskDao,
    private val groupDao: GroupDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainState())
    val uiState = _uiState.asStateFlow()

    private val _allTasks = MutableStateFlow(emptyList<MyTask>())
    val allTasks = _allTasks.asStateFlow()

    private val _allGroups = MutableStateFlow(emptyList<TaskGroup>())
    val allGroups = _allGroups.asStateFlow()

    init {
        viewModelScope.launch {
            taskDao.getAllTasksFlow().collect { tasks ->
                _allTasks.value = tasks
                loadTasks()
            }
        }
        viewModelScope.launch {
            groupDao.getAllGroups().collect { groups ->
                _allGroups.value = groups
            }
        }
    }

    fun onIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.MainSwitch -> toggleTaskCompletion(intent.task)
            is MainIntent.ShowBottomSheet ->
                _uiState.update { it.copy(bottomSheet = intent.type) }

            MainIntent.CloseBottomSheet ->
                _uiState.update { it.copy(bottomSheet = MainBottomSheetType.None) }

            is MainIntent.SelectTask ->
                _uiState.update { it.copy(selectedTaskId = intent.taskId) }

            is MainIntent.SelectGroup ->
                _uiState.update {
                    it.copy(
                        selectedGroupId = intent.group.groupId,
                        selectedGroupName = intent.group.name
                    )
                }

            is MainIntent.SelectTaskTypes ->
                _uiState.update { it.copy(selectedTaskTypes = intent.taskTypes) }

            is MainIntent.ChangeEditMode ->
                _uiState.update { it.copy(isGroupEditMode = intent.isEdit) }

            is MainIntent.LoadTasks -> loadTasks()
            is MainIntent.SelectTab ->
                _uiState.update { it.copy(selectedTabIndex = intent.tabIndex) }
        }
    }

    private fun toggleTaskCompletion(task: MyTask) = viewModelScope.launch {
        _uiState.update { state ->
            state.copy(
                tasks = state.tasks.map {
                    if (it.taskId == task.taskId) {
                        it.copy(isComplete = !it.isComplete)
                    } else {
                        it
                    }
                }
            )
        }
        try {
            taskDao.updateTask(
                task.copy(isComplete = !task.isComplete)
            )
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message) }
        }
    }

    private fun loadTasks() {
        viewModelScope.launch {
            val loadedTasks = when {
                _uiState.value.selectedGroupId > 0 -> {
                    groupDao.getGroupById(_uiState.value.selectedGroupId)?.tasks ?: emptyList()
                }

                _uiState.value.selectedGroupId == -2 -> {
                    allTasks.value.filter { it.isComplete }
                }

                else -> allTasks.value
            }
            _uiState.update {
                var filteredByTabTasks = when (it.selectedTabIndex) {
                    1 -> loadedTasks.filter { !it.isComplete }
                    2 -> loadedTasks.filter { it.isComplete }
                    else -> loadedTasks
                }

                it.selectedTaskTypes.forEach { selectedType ->
                    filteredByTabTasks = filteredByTabTasks.filter { it.type == selectedType.name }
                }

                it.copy(tasks = filteredByTabTasks.sortedBy { it.taskId }.reversed())
            }
        }
    }
}
