package com.example.data.repository

import com.example.data.toMyTask
import com.example.database.dao.TaskDao
import com.example.shared.UserTask
import jakarta.inject.Inject

class TaskRepository @Inject constructor(private val taskDao: TaskDao) {

    val tasksListFlow = taskDao.getAllTasksFlow()
    val allNotSyncedTasks = taskDao.getAllNotSyncedTasksFlow()

    suspend fun getAllTasksWithoutGroups(): List<UserTask> {
        return taskDao.getAllTasksWithoutGroups()
    }

    suspend fun getTaskById(taskId: Int): UserTask? {
        return taskDao.getTaskById(taskId)
    }

    suspend fun deleteTaskById(taskId: Int): Int? {
        return taskDao.deleteTaskById(taskId)
    }

    suspend fun insertTask(task: UserTask): Long {
        return taskDao.insertTask(task.toMyTask())
    }

    suspend fun insertWithSyncFlag(task: UserTask) {
        taskDao.insertWithSyncFlag(task.toMyTask())
    }

    suspend fun updateWithSyncFlag(task: UserTask) {
        taskDao.updateWithSyncFlag(task.toMyTask())
    }

    suspend fun updateTask(task: UserTask) {
        taskDao.updateTask(task.toMyTask())
    }

    suspend fun toggleTaskCompletion(task: UserTask) {
        taskDao.updateTask(task.toggleCompletion().toMyTask())
    }

    suspend fun syncAllTasks() = taskDao.syncAllTasks()
}