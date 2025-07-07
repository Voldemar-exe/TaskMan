package com.example.control.group

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.control.ControlIntent
import com.example.control.ControlState
import com.example.control.ControlViewModel
import com.example.control.GroupControlIntent
import com.example.data.repository.GroupRepository
import com.example.shared.IntentResult
import com.example.shared.SharedTaskGroup
import com.example.shared.UserGroupWithTasks
import com.example.shared.UserTask
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class GroupControlViewModel @Inject constructor(
    private val groupRepository: GroupRepository
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

    private fun addTask(task: UserTask) {
        controlState.update {
            it.copy(group = groupState.copy(tasksInGroup = groupState.tasksInGroup + task))
        }
    }

    private fun removeTask(task: UserTask) {
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
                val group = stateToSharedGroup()
                val groupId = pushGroupToDatabase(group)
                resolveGroupCrossRef(groupId)

                controlState.update { it.copy(base = baseState.copy(entityId = groupId)) }
                setResult(IntentResult.Success(ControlIntent.SaveEntity.toString()))
            } catch (e: Exception) {
                errorException(e)
            }
        }
    }

    private fun stateToSharedGroup() = SharedTaskGroup(
        localId = 0,
        serverId = baseState.serverEntityId,
        name = baseState.entityName,
        icon = baseState.selectedIcon,
        color = baseState.selectedColor
    )

    private suspend fun pushGroupToDatabase(groupEntity: SharedTaskGroup): Int {
        if (baseState.isEditMode) {
            groupRepository.updateGroup(groupEntity.copy(localId = baseState.entityId!!))
            return baseState.entityId!!
        } else {
            return groupRepository.insertGroup(groupEntity).toInt()
        }
    }

    private suspend fun resolveGroupCrossRef(groupId: Int) {
        groupRepository.deleteAllCrossRefsForGroup(groupId)
        groupState.tasksInGroup.forEach { task ->
            groupRepository.insertGroupTaskCrossRef(groupId, task.localId)
        }
    }

    override fun loadEntity(entityId: Int) {
        viewModelScope.launch {
            if (baseState.entityId == entityId) return@launch
            startLoading()
            try {
                groupRepository.getGroupById(entityId)?.let { groupWithTasks ->
                    updateStateFromGroup(groupWithTasks)
                }
            } catch (e: Exception) {
                errorException(e)
            }
        }
    }

    private fun updateStateFromGroup(groupWithTasks: UserGroupWithTasks) {
        controlState.update { state ->
            val base = baseState.copy(
                entityId = groupWithTasks.group.localId,
                serverEntityId = groupWithTasks.group.serverId,
                entityName = groupWithTasks.group.name,
                selectedIcon = groupWithTasks.group.icon,
                selectedColor = groupWithTasks.group.color,
                isLoading = false,
                intentRes = IntentResult.Success(
                    ControlIntent.LoadEntity(groupWithTasks.group.localId).toString()
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
                groupRepository.deleteAllCrossRefsForGroup(entityId)
                groupRepository.deleteGroupById(entityId)
            }

            setResult(IntentResult.Success(ControlIntent.DeleteEntity(entityId).toString()))
        }
    }
}
