package com.example.sync

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.data.repository.SyncRepository
import com.example.network.retrofit.sync.SyncService
import jakarta.inject.Inject

class SyncWorkerFactory @Inject constructor(
    private val syncRepository: SyncRepository,
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
                syncRepository = syncRepository,
                syncService = service
            )

            else -> null
        }
    }
}
