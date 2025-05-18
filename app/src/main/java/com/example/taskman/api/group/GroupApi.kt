package com.example.taskman.api.group

import com.example.shared.dto.GroupDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface GroupApi {

    @POST("/groups")
    suspend fun create(@Body dto: GroupDto): Response<Int>

    @PUT("/groups/{id}")
    suspend fun update(@Path("id") id: Int, @Body dto: GroupDto): Response<Unit>

    @DELETE("/groups/{id}")
    suspend fun delete(@Path("id") id: Int): Response<Unit>

}