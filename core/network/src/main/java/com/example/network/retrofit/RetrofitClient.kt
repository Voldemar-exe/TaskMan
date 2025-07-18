package com.example.network.retrofit

import com.example.data.TokenProvider
import com.example.network.retrofit.auth.AuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val SERVER_URL = "http://10.0.2.2:8080/"
    private lateinit var retrofit: Retrofit
    private lateinit var okHttpClient: OkHttpClient

    fun init(tokenProvider: TokenProvider): Retrofit {
        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenProvider))
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit
    }
}