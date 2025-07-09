package com.example.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.repository.SyncRepository
import com.example.network.retrofit.sync.SyncService
import com.example.shared.SharedGroupWithTasks
import com.example.shared.UserGroupWithTasks
import com.example.shared.UserTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.serialization.json.Json

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncRepository: SyncRepository,
    private val syncService: SyncService,
) : CoroutineWorker(context, workerParams) {
    companion object {
        private const val TAG = "SyncWorker"
    }

    override suspend fun doWork(): Result =
        try {
            inputData.getString("tasks")
                ?.let {
                    syncTasks(
                        Json.decodeFromString<List<UserTask>>(it)
                    )
                }
            inputData.getString("groups")
                ?.let {
                    syncGroups(
                        Json.decodeFromString<List<UserGroupWithTasks>>(it)
                    )
                }
            Result.failure()
        } catch (e: Exception) {
            Log.e(TAG, "Fail sync, error: ${e.message}")
            Result.retry()
        }

    private suspend fun syncTasks(tasksToSync: List<UserTask>): Result {
        val allTaskIds = tasksToSync.map { it.serverId ?: 0 }

        if (tasksToSync.isNotEmpty()) {
            val updatedTasks = syncService.syncTasks(tasksToSync, allTaskIds)
            return if (updatedTasks.isNotEmpty()) {
                val updatedLocalTasks = tasksToSync.mapIndexed { index, localTask ->
                    val remoteTask = updatedTasks[index]
                    localTask.updateServerId(remoteTask.serverId!!)
                }
                syncRepository.updateTasksByDataFromServer(updatedLocalTasks)
                Result.success()
            } else {
                Result.failure()
            }
        }
        return Result.failure()
    }

    private suspend fun syncGroups(groupsToSync: List<UserGroupWithTasks>): Result {
        val allGroupIds = groupsToSync.map { it.group.serverId ?: 0 }

        if (groupsToSync.isNotEmpty()) {
            val updatedGroups = syncService.syncGroups(
                groupsToSync,
                allGroupIds,
            )
            Log.d(TAG, "$updatedGroups")
            return if (updatedGroups.isNotEmpty()) {
                val updatedLocalGroups =
                    groupsToSync.mapIndexed { index, localGroupWithTasks ->
                        val remoteGroup = updatedGroups[index]
                        val group = localGroupWithTasks.group.updateServerId(
                            remoteGroup.group.serverId!!
                        )
                        val tasks = localGroupWithTasks.tasks.map { remoteTask ->
                                remoteTask.updateServerId(remoteTask.serverId!!)
                            }
                        SharedGroupWithTasks(group, tasks)
                    }
                syncRepository.updateGroupsWithTaskFromServer(updatedLocalGroups)
                Result.success()
            } else {
                Result.failure()
            }
        }
        return Result.failure()
    }
}
