package com.example.taskman.api.group

import android.util.Log
import com.example.taskman.api.task.TaskDto
import retrofit2.Response

class GroupService(
    private val apiClient: GroupApi
) {

    suspend fun getAllGroups(): List<GroupDto>? =
        safeApiCall { apiClient.getAll() }

    suspend fun createGroup(request: GroupRequest): Int? {
        return safeApiCall { apiClient.create(request) }
    }

    suspend fun updateGroup(id: Int, request: GroupRequest): Boolean {
        return try {
            Log.d(TAG, "Updating group $id: $request")
            val response = apiClient.update(id, request)
            if (response.isSuccessful) {
                Log.d(TAG, "Group updated successfully")
                true
            } else {
                Log.e(TAG, "Failed to update group: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating group", e)
            false
        }
    }

    suspend fun deleteGroup(id: Int): Boolean {
        return try {
            Log.d(TAG, "Deleting group with ID: $id")
            val response = apiClient.delete(id)
            if (response.isSuccessful) {
                Log.d(TAG, "Group deleted successfully")
                true
            } else {
                Log.e(TAG, "Failed to delete group: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting group", e)
            false
        }
    }

    suspend fun syncTasksForGroup(groupId: Int, taskIds: List<Int>) =
        safeApiCall { apiClient.syncTasksForGroup(groupId, taskIds) }

    suspend fun getTasksInGroup(groupId: Int): List<TaskDto>? =
        safeApiCall { apiClient.getTasksInGroup(groupId) }

    private suspend fun <T> safeApiCall(call: suspend () -> Response<T>): T? {
        return try {
            val response = call()
            if (response.isSuccessful) response.body()
            else {
                Log.e(TAG, "API error: ${response.code()} ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during API call", e)
            null
        }
    }

    companion object {
        private const val TAG = "GroupService"
    }
}