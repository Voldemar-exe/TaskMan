package com.example.taskman.api.profile

import retrofit2.Response
import retrofit2.http.DELETE

interface ProfileApi {

    @DELETE("/users/data")
    suspend fun deleteUserData(): Response<Unit>

    @DELETE("/users")
    suspend fun deleteUser(): Response<Unit>
}