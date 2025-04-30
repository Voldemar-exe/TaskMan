package com.example.taskman.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.taskman.model.MyTask
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY date DESC")
    fun getAllTasksFlow(): Flow<List<MyTask>>

    @Query("SELECT * FROM tasks ORDER BY date DESC")
    suspend fun getAllTasksList(): List<MyTask>

    @Query("SELECT * FROM tasks WHERE taskId = :taskId")
    suspend fun getTaskById(taskId: Int): MyTask?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: MyTask)

    @Update
    suspend fun updateTask(task: MyTask)

    @Delete
    suspend fun deleteTask(task: MyTask)
}