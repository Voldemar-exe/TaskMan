package com.example.taskman.api.profile

import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Path

interface ProfileApi {

    @DELETE("users/{login}/data")
    suspend fun deleteUserData(@Path("login") login: String): Response<Unit>

    @DELETE("users/{login}")
    suspend fun deleteUser(@Path("login") login: String): Response<Unit>
}