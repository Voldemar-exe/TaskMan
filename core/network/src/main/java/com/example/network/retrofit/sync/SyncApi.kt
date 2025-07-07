package com.example.network.retrofit.sync

import com.example.network.SyncRequest
import com.example.network.SyncResponse
import com.example.shared.UserGroupWithTasks
import com.example.shared.UserTask
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SyncApi {

    /*
    TODO USE DTO
    @POST("/sync/tasks")
    suspend fun syncTasks(@Body request: SyncRequest<TaskDto>): Response<SyncResponse<TaskDto>>

    @POST("/sync/groups")
    suspend fun syncGroups(@Body request: SyncRequest<GroupDto>): Response<SyncResponse<GroupDto>>
    */

    @POST("/sync/tasks")
    suspend fun syncTasks(
        @Body request: SyncRequest<UserTask>
    ): Response<SyncResponse<UserTask>>

    @POST("/sync/groups")
    suspend fun syncGroups(
        @Body request: SyncRequest<UserGroupWithTasks>
    ): Response<SyncResponse<UserGroupWithTasks>>
}
