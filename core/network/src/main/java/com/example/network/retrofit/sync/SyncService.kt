package com.example.network.retrofit.sync

import android.util.Log
import com.example.network.SyncRequest
import com.example.shared.UserGroupWithTasks
import com.example.shared.UserTask
import jakarta.inject.Inject
import retrofit2.Response

// TODO USE DTO
class SyncService @Inject constructor(
    private val apiClient: SyncApi,
) {
    companion object {
        private const val TAG = "SyncService"
    }

    suspend fun syncTasks(
        tasksToServer: List<UserTask>,
        allTasksIds: List<Int>,
    ): List<UserTask> =
        safeApiCall {
            apiClient.syncTasks(SyncRequest(tasksToServer, allTasksIds))
        }?.updatedEntities ?: emptyList()

    suspend fun syncGroups(
        groupsToServer: List<UserGroupWithTasks>,
        allGroupsIds: List<Int>,
    ): List<UserGroupWithTasks> =
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
