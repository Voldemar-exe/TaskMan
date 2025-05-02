package com.example.taskman.api.task

import com.example.taskman.api.RetrofitClient

object TaskClient {
    val instance: TaskApi by lazy {
        RetrofitClient.retrofit.create(TaskApi::class.java)
    }
}