package com.example.taskman.api.group

import com.example.taskman.api.task.TaskDto
import io.ktor.http.HttpStatusCode
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface GroupApi {

    @GET("/groups")
    suspend fun getAll(): Response<List<GroupDto>>

    @POST("/groups")
    suspend fun create(@Body request: GroupRequest): Response<Int>

    @PUT("/groups/{id}")
    suspend fun update(@Path("id") id: Int, @Body request: GroupRequest): Response<Unit>

    @DELETE("/groups/{id}")
    suspend fun delete(@Path("id") id: Int): Response<HttpStatusCode>

    @GET("/groups/{id}/tasks")
    suspend fun getTasksInGroup(@Path("id") id: Int): Response<List<TaskDto>>

    @POST("/groups/{id}/tasks")
    suspend fun syncTasksForGroup(
        @Path("id") groupId: Int,
        @Body tasks: List<Int>
    ): Response<HttpStatusCode>

}