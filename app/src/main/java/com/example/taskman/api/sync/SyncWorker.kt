package com.example.taskman.api.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.shared.dto.GroupDto
import com.example.taskman.db.TaskManDatabase

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val db: TaskManDatabase,
    private val syncService: SyncService
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "SyncWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            syncTasks()
            syncGroups()
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Fail sync, error: ${e.message}")
            Result.retry()
        }
    }

    private suspend fun syncTasks() {
        val tasksToSync = db.taskDao().getAllNotSyncedTasksList()
        val allTaskIds = tasksToSync.map { it.serverId ?: 0 }

        if (tasksToSync.isNotEmpty()) {
            val updatedTasks = syncService.syncTasks(tasksToSync.map { it.toDto() }, allTaskIds)
            if (updatedTasks.isNotEmpty()) {
                val updatedLocalTasks = tasksToSync.zip(updatedTasks) { localTask, remoteTask ->
                    localTask.copy(
                        serverId = remoteTask.id,
                        isSynced = true
                    )
                }
                db.taskDao().updateTasksByDataFromServer(updatedLocalTasks)
            } else {
                error("Server fail to sync tasks")
            }
        }
    }

    private suspend fun syncGroups() {
        val groupsToSync = db.groupDao().getAllNotSyncedList()
        val allGroupIds = groupsToSync.map { it.group.serverId ?: 0 }

        if (groupsToSync.isNotEmpty()) {
            val updatedGroups = syncService.syncGroups(
                groupsToSync.map {
                    GroupDto(
                        it.group.serverId ?: 0,
                        it.group.name,
                        it.group.icon,
                        it.group.color,
                        it.tasks.map { it.toDto() }
                    )
                },
                allGroupIds
            )
            Log.d(TAG, "$updatedGroups")
            if (updatedGroups.isNotEmpty()) {
                val updatedLocalGroups =
                    groupsToSync.zip(updatedGroups) { localGroup, remoteGroup ->
                        localGroup.copy(
                            group = localGroup.group.copy(
                                serverId = remoteGroup.id,
                                isSynced = true
                            ),
                            tasks =
                                localGroup.tasks.zip(remoteGroup.tasks) { localTask, remoteTask ->
                                    localTask.copy(
                                        serverId = remoteTask.id,
                                        isSynced = true
                                    )
                                }
                        )
                    }
                db.groupDao().updateGroupsWithTaskFromServer(updatedLocalGroups)
            } else {
                error("Server fail to sync groups")
            }
        }
    }
}
