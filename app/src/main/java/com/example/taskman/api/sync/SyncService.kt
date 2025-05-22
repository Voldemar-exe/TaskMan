package com.example.taskman.api.sync

import android.util.Log
import com.example.shared.dto.GroupDto
import com.example.shared.dto.TaskDto
import com.example.shared.request.SyncRequest
import retrofit2.Response

class SyncService(
    private val apiClient: SyncApi,
) {
    companion object {
        private const val TAG = "SyncService"
    }

    suspend fun syncTasks(
        tasksToServer: List<TaskDto>,
        allTasksIds: List<Int>,
    ): List<TaskDto> =
        safeApiCall {
            apiClient.syncTasks(SyncRequest(tasksToServer, allTasksIds))
        }?.updatedEntities ?: emptyList()

    suspend fun syncGroups(
        groupsToServer: List<GroupDto>,
        allGroupsIds: List<Int>,
    ): List<GroupDto> =
        safeApiCall {
            apiClient.syncGroups(SyncRequest(groupsToServer, allGroupsIds))
        }?.updatedEntities ?: emptyList()

    private suspend fun <T> safeApiCall(call: suspend () -> Response<T>): T? =
        try {
            val response = call()
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e(TAG, "API error: ${response.code()} ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during API call", e)
            null
        }
}
