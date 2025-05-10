package com.example.taskman.api

import com.example.taskman.api.auth.AuthInterceptor
import com.example.taskman.ui.auth.AuthStorage
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val SERVER_URL = "http://10.0.2.2:8080/"
    private lateinit var retrofit: Retrofit
    private lateinit var okHttpClient: OkHttpClient

    fun init(authStorage: AuthStorage): RetrofitClient {
        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(authStorage))
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return this
    }

    fun <T> service(clazz: Class<T>): T = retrofit.create(clazz)
}