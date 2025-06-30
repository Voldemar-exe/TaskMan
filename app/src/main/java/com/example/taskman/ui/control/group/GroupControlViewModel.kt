package com.example.taskman.ui.control.group

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.viewModelScope
import com.example.taskman.db.GroupDao
import com.example.taskman.db.GroupTaskCrossRef
import com.example.taskman.db.GroupWithTasks
import com.example.taskman.model.MyTask
import com.example.taskman.model.TaskGroup
import com.example.taskman.ui.components.IntentResult
import com.example.taskman.ui.control.ControlIntent
import com.example.taskman.ui.control.ControlState
import com.example.taskman.ui.control.ControlViewModel
import com.example.taskman.ui.control.GroupControlIntent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroupControlViewModel(
    private val groupDao: GroupDao
) : ControlViewModel(
    initialState = ControlState(
        base = ControlState.BaseState(),
        group = ControlState.GroupState()
    )
) {
    companion object {
        private const val TAG = "GroupControlViewModel"
    }

    private val groupState: ControlState.GroupState =
        controlState.value.group ?: error("Task state is null in $TAG")

    fun onIntent(intent: ControlIntent) {
        Log.i(TAG, "Intent: $intent")
        when (intent) {
            is GroupControlIntent.AddTask -> addTask(intent.task)
            is GroupControlIntent.RemoveTask ->
                if (groupState.tasksInGroup.size > 1) removeTask(intent.task)

            else -> processBaseIntent(intent)
        }
    }

    private fun addTask(task: MyTask) {
        controlState.update {
            it.copy(group = groupState.copy(tasksInGroup = groupState.tasksInGroup + task))
        }
    }

    private fun removeTask(task: MyTask) {
        controlState.update {
            it.copy(group = groupState.copy(tasksInGroup = groupState.tasksInGroup - task))
        }
    }

    override fun saveEntity() {
        if (!validateData()) {
            setResult(IntentResult.Error("Data validation error"))
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val group = stateToGroup()
                val groupId = pushGroupToDatabase(group)
                resolveGroupCrossRef(groupId)

                controlState.update { it.copy(base = baseState.copy(entityId = groupId)) }
                setResult(IntentResult.Success(ControlIntent.SaveEntity.toString()))
            } catch (e: Exception) {
                errorException(e)
            }
        }
    }

    private fun stateToGroup() = TaskGroup(
        groupId = 0,
        serverId = baseState.serverEntityId,
        name = baseState.entityName,
        icon = baseState.selectedIcon,
        color = baseState.selectedColor.toArgb().toLong()
    )

    private suspend fun pushGroupToDatabase(groupEntity: TaskGroup): Int {
        if (baseState.isEditMode) {
            groupDao.updateGroup(groupEntity.copy(groupId = baseState.entityId!!))
            return baseState.entityId
        } else {
            return groupDao.insertGroup(groupEntity).toInt()
        }
    }

    private suspend fun resolveGroupCrossRef(groupId: Int) {
        groupDao.deleteAllCrossRefsForGroup(groupId)
        groupState.tasksInGroup.forEach { task ->
            groupDao.insertGroupTaskCrossRef(
                GroupTaskCrossRef(groupId = groupId, taskId = task.taskId)
            )
        }
    }

    override fun loadEntity(entityId: Int) {
        viewModelScope.launch {
            if (baseState.entityId == entityId) return@launch
            startLoading()
            try {
                groupDao.getGroupById(entityId)?.let { groupWithTasks ->
                    updateStateFromGroup(groupWithTasks)
                }
            } catch (e: Exception) {
                errorException(e)
            }
        }
    }

    private fun updateStateFromGroup(groupWithTasks: GroupWithTasks) {
        controlState.update { state ->
            val base = baseState.copy(
                entityId = groupWithTasks.group.groupId,
                serverEntityId = groupWithTasks.group.serverId,
                entityName = groupWithTasks.group.name,
                selectedIcon = groupWithTasks.group.icon,
                selectedColor = Color(groupWithTasks.group.color),
                isLoading = false,
                intentRes = IntentResult.Success(
                    ControlIntent.LoadEntity(groupWithTasks.group.groupId).toString()
                ),
                isEditMode = true
            )
            state.copy(
                base = base,
                group = groupState.copy(tasksInGroup = groupWithTasks.tasks)
            )
        }
    }

    override fun deleteEntity(entityId: Int) {
        viewModelScope.launch {
            startLoading()
            Log.i(TAG, "Start Delete")

            withContext(Dispatchers.IO) {
                groupDao.deleteAllCrossRefsForGroup(entityId)
                groupDao.deleteGroupById(entityId)
            }

            setResult(IntentResult.Success(ControlIntent.DeleteEntity(entityId).toString()))
        }
    }
}
