package com.example.taskman.ui.control.group

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.viewModelScope
import com.example.shared.dto.GroupDto
import com.example.shared.dto.TaskDto
import com.example.taskman.api.group.GroupService
import com.example.taskman.db.GroupDao
import com.example.taskman.db.GroupTaskCrossRef
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
    private val groupDao: GroupDao,
    private val groupService: GroupService
) : ControlViewModel(
    initialState = ControlState(
        base = ControlState.BaseState(),
        group = ControlState.GroupState()
    )
) {
    private val groupState: ControlState.GroupState
        get() = controlState.value.group
            ?: throw IllegalStateException("Task state is null in $TAG")

    companion object {
        private const val TAG = "GroupControlViewModel"
    }

    fun onIntent(intent: ControlIntent) {
        Log.i(TAG, "Intent: $intent")
        when (intent) {
            is GroupControlIntent.AddTask -> addTask(intent.task)
            is GroupControlIntent.RemoveTask -> removeTask(intent.task)
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
            if (groupState.tasksInGroup.size < 2) return
            it.copy(group = groupState.copy(tasksInGroup = groupState.tasksInGroup - task))
        }
    }

    override fun saveEntity() {
        if (!validateData()) return

        viewModelScope.launch {
            startLoading()

            val base = controlState.value.base
            val state = controlState.value.group!!

            try {
                val groupId = withContext(Dispatchers.IO) {
                    if (base.isEditMode) {
                        val entity = TaskGroup(
                            groupId = base.entityId!!,
                            serverId = base.serverEntityId,
                            name = base.entityName,
                            icon = base.selectedIcon,
                            color = base.selectedColor.toArgb().toLong()
                        )
                        groupDao.updateGroup(entity)
                        entity.groupId
                    } else {
                        val entity = TaskGroup(
                            serverId = base.serverEntityId,
                            name = base.entityName,
                            icon = base.selectedIcon,
                            color = base.selectedColor.toArgb().toLong()
                        )
                        groupDao.insertGroup(entity).toInt()
                    }
                }

                withContext(Dispatchers.IO) {
                    groupDao.deleteAllCrossRefsForGroup(groupId)
                    state.tasksInGroup.forEach { task ->
                        groupDao.insertGroupTaskCrossRef(
                            GroupTaskCrossRef(groupId = groupId, taskId = task.taskId)
                        )
                    }
                }

                controlState.update { it.copy(base = base.copy(entityId = groupId)) }

                syncToServer()

                setResult(IntentResult.Success(ControlIntent.SaveEntity.toString()))

            } catch (e: Exception) {
                errorException(e)
            }
        }
    }

    override fun loadEntity(entityId: Int) {
        viewModelScope.launch {
            startLoading()
            try {
                withContext(Dispatchers.IO) {
                    groupDao.getGroupById(entityId)
                }?.let { gw ->
                    controlState.update { state ->
                        val base = state.base.copy(
                            entityId = gw.group.groupId,
                            entityName = gw.group.name,
                            selectedIcon = gw.group.icon,
                            selectedColor = Color(gw.group.color),
                            isLoading = false,
                            intentRes = IntentResult.Success(
                                ControlIntent.LoadEntity(entityId).toString()
                            ),
                            isEditMode = true
                        )
                        state.copy(
                            base = base,
                            group = state.group!!.copy(tasksInGroup = gw.tasks)
                        )
                    }
                }
            } catch (e: Exception) {
                errorException(e)
            }
        }
    }

    override fun deleteEntity(entityId: Int) {
        viewModelScope.launch {
            startLoading()
            try {

                withContext(Dispatchers.IO) {
                    groupDao.deleteAllCrossRefsForGroup(entityId)
                    groupDao.deleteGroupById(entityId)
                }

                groupService.deleteGroup(entityId)

                setResult(IntentResult.Success(ControlIntent.DeleteEntity(entityId).toString()))
            } catch (e: Exception) {
                errorException(e)
            }
        }
    }

    private suspend fun syncToServer(): Boolean {

        val reqGroup = GroupDto(
            id = baseState.entityId ?: 0,
            name = baseState.entityName,
            icon = baseState.selectedIcon,
            color = baseState.selectedColor.toArgb().toLong(),
            tasks = groupState.tasksInGroup.map { task ->
                TaskDto(
                    id = task.serverId ?: 0,
                    name = task.name,
                    icon = task.icon,
                    color = task.color,
                    type = task.type,
                    note = task.note,
                    isComplete = task.isComplete,
                    date = task.date
                )
            }
        )

        if (baseState.isEditMode) {
            baseState.serverEntityId?.let {
                return groupService.updateGroup(it, reqGroup)
            }
        } else {
            return groupService.createGroup(reqGroup) != null
        }
        return false
    }
}