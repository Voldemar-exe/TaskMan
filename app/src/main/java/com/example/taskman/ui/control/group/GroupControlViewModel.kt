package com.example.taskman.ui.control.group

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.viewModelScope
import com.example.taskman.db.GroupDao
import com.example.taskman.db.GroupTaskCrossRef
import com.example.taskman.model.MyTask
import com.example.taskman.model.TaskGroup
import com.example.taskman.ui.components.IntentResult
import com.example.taskman.ui.control.ControlIntent
import com.example.taskman.ui.control.ControlState
import com.example.taskman.ui.control.ControlViewModel
import com.example.taskman.ui.control.GroupControlIntent
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GroupControlViewModel(
    private val groupDao: GroupDao
) : ControlViewModel(
    initialState = ControlState(
        base = ControlState.BaseState(),
        group = ControlState.GroupState()
    )
) {

    fun onIntent(intent: ControlIntent) {
        Log.i(TAG, "intent: $intent")
        viewModelScope.launch {
            when (intent) {
                is GroupControlIntent.AddTask -> addTask(intent.task)
                is GroupControlIntent.RemoveTask -> removeTask(intent.task)
                else -> processBaseIntent(intent)
            }
        }
    }

    private fun addTask(task: MyTask) {
        controlState.value.group?.let { groupState ->
            controlState.update { controlState ->
                controlState.copy(
                    group = groupState.copy(
                        tasksInGroup = groupState.tasksInGroup + task
                    )
                )
            }
        }
    }

    private fun removeTask(task: MyTask) {
        controlState.value.group?.let { groupState ->
            if (groupState.tasksInGroup.size < 2) return
            controlState.update { controlState ->
                controlState.copy(
                    group = groupState.copy(
                        tasksInGroup = groupState.tasksInGroup - task
                    )
                )
            }
        }
    }

    override fun saveEntity() {

        if (!validateData()) return

        viewModelScope.launch {
            startLoading()
            val baseState = controlState.value.base
            controlState.value.group?.let { groupState ->
                try {
                    val groupId = if (baseState.isEditMode) {
                        val group = TaskGroup(
                            groupId = baseState.entityId ?: 0,
                            name = baseState.entityName,
                            icon = baseState.selectedIcon,
                            color = baseState.selectedColor.toArgb().toLong()
                        )
                        groupDao.updateGroup(group)
                        group.groupId
                    } else {
                        val newGroupId = groupDao.insertGroup(
                            TaskGroup(
                                name = baseState.entityName,
                                icon = baseState.selectedIcon,
                                color = baseState.selectedColor.toArgb().toLong()
                            )
                        )
                        newGroupId.toInt()
                    }

                    groupDao.deleteAllCrossRefsForGroup(groupId)

                    groupState.tasksInGroup.forEach { task ->
                        groupDao.insertGroupTaskCrossRef(
                            GroupTaskCrossRef(groupId = groupId, taskId = task.taskId)
                        )
                    }
                    setResult(IntentResult.Success(ControlIntent.SaveEntity.toString()))
                } catch (e: Exception) {
                    errorException(e)
                }
            }
        }
    }

    override fun loadEntity(entityId: Int) {

        viewModelScope.launch {
            startLoading()

            val baseState = controlState.value.base
            controlState.value.group?.let { groupState ->
                try {
                    groupDao.getGroupById(entityId)?.let { groupWithTasks ->
                        controlState.update {
                            it.copy(
                                base = baseState.copy(
                                    entityId = groupWithTasks.group.groupId,
                                    entityName = groupWithTasks.group.name,
                                    selectedIcon = groupWithTasks.group.icon,
                                    selectedColor = Color(groupWithTasks.group.color),
                                    isLoading = false,
                                    intentRes = IntentResult.Success(
                                        ControlIntent.LoadEntity(entityId).toString()
                                    ),
                                    isEditMode = true
                                ),
                                group = groupState.copy(
                                    tasksInGroup = groupWithTasks.tasks,
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    errorException(e)
                }
            }
        }
    }
}