package com.example.sync

import android.util.Log
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.data.repository.GroupRepository
import com.example.data.repository.TaskRepository
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class SyncManager @Inject constructor(
    private val taskRepository: TaskRepository,
    private val groupRepository: GroupRepository,
    private val workManager: WorkManager
) {
    companion object {
        private const val TAG = "SyncRepository"
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        Log.i(TAG, "INIT")
        observeNotSyncedData()
    }

    private fun observeNotSyncedData() {
        scope.launch {
            taskRepository.allNotSyncedTasks.collect { tasks ->
                Log.i(TAG, "$tasks")
                if (tasks.isNotEmpty()) {
                    scheduleSync("tasks", tasks)
                }
            }
        }
        scope.launch {
            groupRepository.allGroupsNotSyncedFlow.collect { groups ->
                Log.i(TAG, "$groups")
                if (groups.isNotEmpty()) {
                    scheduleSync("groups", groups)
                }
            }
        }
    }

    // TODO REMOVE "Any"
    private fun scheduleSync(key: String, dataToSync: List<Any>) {

        val serializedData = try {
            Json.encodeToString(dataToSync)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to serialize data for key $key", e)
            return
        }

        val inputData = Data.Builder()
            .putString(key, serializedData)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setInputData(inputData)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        workManager.enqueueUniqueWork(
            "SyncWork",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

}
