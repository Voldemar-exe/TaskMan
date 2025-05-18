package com.example.taskman.api.sync

import com.example.shared.dto.GroupDto
import com.example.shared.dto.TaskDto
import com.example.shared.request.SyncRequest
import com.example.shared.response.SyncResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SyncApi {

    @POST("/sync/tasks")
    suspend fun syncTasks(@Body request: SyncRequest<TaskDto>): Response<SyncResponse<TaskDto>>

    @POST("/sync/groups")
    suspend fun syncGroups(@Body request: SyncRequest<GroupDto>): Response<SyncResponse<GroupDto>>
}
