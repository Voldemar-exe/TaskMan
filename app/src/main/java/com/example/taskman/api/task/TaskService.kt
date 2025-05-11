package com.example.taskman.api.task

import android.util.Log
import com.example.shared.dto.TaskDto
import retrofit2.Response

class TaskService(
    private val apiClient: TaskApi
) {

    suspend fun getAllTasks(): List<TaskDto>? {
        return safeApiCall { apiClient.getAll() }
    }

    suspend fun createTask(request: TaskRequest): Int? {
        return safeApiCall { apiClient.create(request) }
    }

    suspend fun updateTask(request: TaskRequest): Boolean {
        return try {
            Log.d(TAG, "Updating task: $request")
            val response = apiClient.update(request.id, request)
            if (response.isSuccessful) {
                Log.d(TAG, "Task updated successfully")
                true
            } else {
                Log.e(TAG, "Failed to update task: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating task", e)
            false
        }
    }

    suspend fun deleteTask(id: Int): Boolean {
        return try {
            Log.d(TAG, "Deleting task with ID: $id")
            val response = apiClient.delete(id)
            if (response.isSuccessful) {
                Log.d(TAG, "Task deleted successfully")
                true
            } else {
                Log.e(TAG, "Failed to delete task: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting task", e)
            false
        }
    }

    private suspend fun <T> safeApiCall(call: suspend () -> Response<T>): T? {
        return try {
            val response = call()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            Log.e(TAG, "API error", e)
            null
        }
    }

    companion object {
        private const val TAG = "TaskService"
    }
}