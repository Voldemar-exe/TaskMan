package com.example.taskman.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MyTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "Test",
    val type: String = "Type",
    val note: String = "Note",
    val isComplete: Boolean = false
    // TODO add more val
)
