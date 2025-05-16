package com.example.taskman.api.profile

import com.example.taskman.api.RetrofitClient

object ProfileClient {
    val instance: ProfileApi by lazy {
        RetrofitClient.service(ProfileApi::class.java)
    }
}