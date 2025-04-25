package com.example.taskman.ui.group

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskman.db.GroupDao
import com.example.taskman.db.GroupTaskCrossRef
import com.example.taskman.model.MyTask
import com.example.taskman.model.TaskGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GroupControlViewModel(
    private val groupDao: GroupDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupControlState())
    val uiState: StateFlow<GroupControlState> = _uiState

    fun processIntent(intent: GroupControlIntent) {
        viewModelScope.launch {
            when (intent) {
                is GroupControlIntent.UpdateName -> updateGroupName(intent.name)
                is GroupControlIntent.UpdateIcon -> updateIcon(intent.icon)
                is GroupControlIntent.UpdateColor -> updateColor(intent.color)
                is GroupControlIntent.AddTask -> addTask(intent.task)
                is GroupControlIntent.RemoveTask -> removeTask(intent.task)
                is GroupControlIntent.LoadGroup -> loadGroup(intent.groupId)
                GroupControlIntent.SaveGroup -> saveGroup()
                GroupControlIntent.ClearGroup -> clearGroup()
            }
        }
    }

    private fun updateGroupName(name: String) {
        _uiState.update { it.copy(groupName = name) }
    }

    private fun updateIcon(icon: Int) {
        _uiState.update { it.copy(selectedIcon = icon) }
    }

    private fun updateColor(color: Color) {
        _uiState.update { it.copy(selectedColor = color) }
    }

    private fun addTask(task: MyTask) {
        _uiState.update { it.copy(tasksInGroup = it.tasksInGroup + task) }
    }

    private fun removeTask(task: MyTask) {
        _uiState.update { it.copy(tasksInGroup = it.tasksInGroup - task) }
    }

    private suspend fun loadGroup(groupId: Int) {
        _uiState.update { it.copy(isLoading = true) }
        try {
            groupDao.getGroupById(groupId)?.let {
                _uiState.update { state ->
                    state.copy(
                        groupId = groupId,
                        groupName = it.group.name,
                        selectedIcon = it.group.icon,
                        selectedColor = Color(it.group.color),
                        tasksInGroup = it.tasks,
                        isLoading = false,
                        error = null,
                        isEditMode = true
                    )
                }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message, isLoading = false) }
        }
    }

    private fun clearGroup() {
        _uiState.update { GroupControlState() }
    }

    private fun saveGroup() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        try {
            val groupId = if (uiState.value.isEditMode) {
                val group = TaskGroup(
                    groupId = uiState.value.groupId!!,
                    name = uiState.value.groupName,
                    icon = uiState.value.selectedIcon,
                    color = uiState.value.selectedColor.toArgb().toLong()
                )
                groupDao.updateGroup(group)
                group.groupId
            } else {
                val newGroupId = groupDao.insertGroup(
                    TaskGroup(
                        name = uiState.value.groupName,
                        icon = uiState.value.selectedIcon,
                        color = uiState.value.selectedColor.toArgb().toLong()
                    )
                )
                newGroupId.toInt()
            }

            groupDao.deleteAllCrossRefsForGroup(groupId)

            uiState.value.tasksInGroup.forEach { task ->
                groupDao.insertGroupTaskCrossRef(
                    GroupTaskCrossRef(groupId = groupId, taskId = task.taskId)
                )
            }
            _uiState.update { it.copy(isLoading = false) }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message, isLoading = false) }
        }
    }
}