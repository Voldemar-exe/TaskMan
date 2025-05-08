package com.example.taskman.api.group

import com.example.taskman.api.RetrofitClient

object GroupClient {
    val instance: GroupApi by lazy {
        RetrofitClient.retrofit.create(GroupApi::class.java)
    }
}