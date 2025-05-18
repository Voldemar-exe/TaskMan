package com.example.taskman.api.sync

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.taskman.db.TaskManDatabase

class SyncWorkerFactory(
    private val dataBase: TaskManDatabase,
    private val service: SyncService
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            SyncWorker::class.java.name -> SyncWorker(
                context = appContext,
                workerParams = workerParameters,
                db = dataBase,
                syncService = service
            )

            else -> null
        }
    }
}