package com.example.taskman.api.sync

import com.example.taskman.api.RetrofitClient

object SyncClient {
    val instance: SyncApi by lazy {
        RetrofitClient.service(SyncApi::class.java)
    }
}