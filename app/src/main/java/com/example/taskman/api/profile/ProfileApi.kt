package com.example.taskman.api.profile

import io.ktor.http.HttpStatusCode
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Path

interface ProfileApi {

    @DELETE("users/{login}/data")
    suspend fun deleteUserData(@Path("login") login: String): Response<HttpStatusCode>

    @DELETE("users/{login}")
    suspend fun deleteUser(@Path("login") login: String): Response<HttpStatusCode>
}