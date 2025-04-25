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
            taskDao.getAllTasks().collect { tasks ->
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

    fun processIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.MainSwitch -> toggleTaskCompletion(intent.task)
            is MainIntent.ShowBottomSheet -> showBottomSheet(intent.type)
            MainIntent.CloseBottomSheet -> closeBottomSheet()
            is MainIntent.SelectTask -> selectTask(intent.taskId)
            is MainIntent.SelectGroup -> selectGroup(intent.group)
            is MainIntent.ChangeEditMode -> changeEditMode(intent.isEdit)
            is MainIntent.LoadTasks -> loadTasks()
        }
    }

    private fun toggleTaskCompletion(task: MyTask) = viewModelScope.launch {
        _uiState.update { state ->
            state.copy(tasks = state.tasks.map {
                if (it.taskId == task.taskId) {
                    it.copy(isComplete = !it.isComplete)
                } else it
            })
        }
        try {
            taskDao.updateTask(
                task.copy(isComplete = !task.isComplete)
            )
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message) }
        }
    }

    private fun showBottomSheet(
        type: MainState.BottomSheetType
    ) {
        _uiState.update {
            it.copy(
                bottomSheet = type
            )
        }
    }

    private fun closeBottomSheet() {
        _uiState.update {
            it.copy(
                bottomSheet = MainState.BottomSheetType.None
            )
        }
    }

    private fun changeEditMode(isEdit: Boolean) {
        _uiState.update {
            it.copy(
                isGroupEditMode = isEdit
            )
        }
    }

    private fun selectTask(taskId: Int) {
        _uiState.update { it.copy(selectedTaskId = taskId) }
    }

    private fun selectGroup(group: TaskGroup) {
        _uiState.update {
            it.copy(
                selectedGroupId = group.groupId,
                selectedGroupName = group.name
            )
        }
    }

    private fun loadTasks() {
        viewModelScope.launch {
            val tasks = when {
                _uiState.value.selectedGroupId != 0 -> {
                    groupDao.getGroupById(_uiState.value.selectedGroupId)?.tasks ?: emptyList()
                }

                else -> allTasks.value
            }
            _uiState.update { it.copy(tasks = tasks) }
        }
    }
}