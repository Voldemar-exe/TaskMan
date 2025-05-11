package com.example.taskman.api.task

import com.example.shared.dto.TaskDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TaskApi {
    @GET("/tasks")
    suspend fun getAll(): Response<List<TaskDto>>

    @POST("/tasks")
    suspend fun create(@Body request: TaskRequest): Response<Int>

    @PUT("/tasks/{id}")
    suspend fun update(@Path("id") id: Int, @Body request: TaskRequest): Response<Unit>

    @DELETE("/tasks/{id}")
    suspend fun delete(@Path("id") id: Int): Response<Unit>
}