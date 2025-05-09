package com.example.db

import com.example.dto.request.TaskDto


interface TaskRepository {
    suspend fun allTasks(login: String): List<TaskDto>
    suspend fun addTask(login: String, task: TaskDto): Boolean
    suspend fun updateTask(login: String, task: TaskDto): Boolean
    suspend fun deleteTask(login: String, id: Int): Boolean
}

