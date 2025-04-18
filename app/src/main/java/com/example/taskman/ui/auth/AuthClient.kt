package com.example.taskman.ui.auth

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object AuthClient {
    private const val SERVER_URL = "http://10.0.2.2:8080/"

    val instance: AuthApi by lazy {

        val client = OkHttpClient.Builder()
            .build()

        Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }
}