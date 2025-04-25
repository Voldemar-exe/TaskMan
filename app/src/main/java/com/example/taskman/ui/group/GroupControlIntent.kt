package com.example.taskman.ui.group

import androidx.compose.ui.graphics.Color
import com.example.taskman.model.MyTask

sealed interface GroupControlIntent {
    data class UpdateName(val name: String) : GroupControlIntent
    data class UpdateIcon(val icon: Int) : GroupControlIntent
    data class UpdateColor(val color: Color) : GroupControlIntent
    data class AddTask(val task: MyTask) : GroupControlIntent
    data class RemoveTask(val task: MyTask) : GroupControlIntent
    object SaveGroup : GroupControlIntent
    object ClearGroup : GroupControlIntent
    data class LoadGroup(val groupId: Int) : GroupControlIntent
}