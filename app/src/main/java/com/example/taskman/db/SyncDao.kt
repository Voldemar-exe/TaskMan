package com.example.taskman.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.example.taskman.model.MyTask

@Dao
interface SyncDao {

    @Query("SELECT * FROM tasks ORDER BY date DESC")
    suspend fun getAllTasksList(): List<MyTask>

    @Update
    fun updateTask(task: MyTask)


}