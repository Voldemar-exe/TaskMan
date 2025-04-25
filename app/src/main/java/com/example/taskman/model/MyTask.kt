package com.example.taskman.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class MyTask(
    @PrimaryKey(autoGenerate = true) val taskId: Int = 0,
    val name: String,
    val icon: Int,
    val color: Long,
    val type: String,
    val note: String,
    val isComplete: Boolean,
    val date: Long
)
