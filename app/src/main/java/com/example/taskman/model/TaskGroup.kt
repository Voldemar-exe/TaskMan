package com.example.taskman.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class TaskGroup(
    @PrimaryKey(autoGenerate = true) val groupId: Int = 0,
    val name: String,
    val icon: Int,
    val color: Long
)