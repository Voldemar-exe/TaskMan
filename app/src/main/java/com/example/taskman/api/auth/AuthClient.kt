package com.example.taskman.api.auth

import com.example.taskman.api.RetrofitClient


object AuthClient {
    val instance: AuthApi by lazy {
        RetrofitClient.service(AuthApi::class.java)
    }
}