package com.example.taskman.api.task

/*
class SyncTaskWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val taskApi: TaskApi by lazy {
        AppApplication.get().taskApiService
    }

    private val taskDao: TaskDao by lazy {
        MyApplication.get().database.taskDao()
    }

    override suspend fun doWork(): Result {
        val taskId = inputData.getInt("taskId", -1)

        if (taskId == -1) return Result.failure()

        val task = taskDao.getTaskById(taskId) ?: return Result.failure()

        return try {
            taskApi.addTask(task.toDto())
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}*/
