package com.example.taskman.model

data class MyTask(
    val name: String = "Test",
    val type: String = "Type",
    val note: String = "Note",
    val isComplete: Boolean = false
    // TODO add more val
)
