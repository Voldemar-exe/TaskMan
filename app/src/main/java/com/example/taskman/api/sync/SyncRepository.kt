package com.example.taskman.api.sync

import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.taskman.db.GroupDao
import com.example.taskman.db.TaskDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SyncRepository(
    private val taskDao: TaskDao,
    private val groupDao: GroupDao,
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
            taskDao.getAllNotSyncedTasksFlow().collect { tasks ->
                Log.i(TAG, "$tasks")
                if (tasks.isNotEmpty()) {
                    scheduleSync()
                }
            }
        }
        scope.launch {
            groupDao.getAllNotSyncedFlow().collect { groups ->
                Log.i(TAG, "$groups")
                if (groups.isNotEmpty()) {
                    scheduleSync()
                }
            }
        }
    }

    private fun scheduleSync() {
        val workRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        workManager.enqueueUniqueWork("SyncWork", ExistingWorkPolicy.REPLACE, workRequest)
    }
}
