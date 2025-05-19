package com.example.taskman.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.taskman.model.MyTask
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY date DESC")
    fun getAllTasksFlow(): Flow<List<MyTask>>

    @Query("SELECT * FROM tasks ORDER BY date DESC")
    suspend fun getAllTasksList(): List<MyTask>

    @Query(
        "SELECT * FROM tasks WHERE taskId NOT IN " +
                "(SELECT taskId FROM group_task) ORDER BY date DESC"
    )
    suspend fun getAllTasksWithoutGroups(): List<MyTask>

    @Query("SELECT * FROM tasks WHERE isSynced = 0")
    suspend fun getAllNotSyncedTasksList(): List<MyTask>

    @Query("SELECT * FROM tasks WHERE isSynced = 0")
    fun getAllNotSyncedTasksFlow(): Flow<List<MyTask>>

    @Query("SELECT * FROM tasks WHERE taskId = :taskId")
    suspend fun getTaskById(taskId: Int): MyTask?

    @Query("DELETE FROM tasks WHERE taskId = :taskId")
    suspend fun deleteTaskById(taskId: Int): Int?

    @Insert
    suspend fun insertTask(task: MyTask): Long

    @Transaction
    suspend fun insertWithSyncFlag(task: MyTask) {
        insertTask(task.copy(isSynced = false))
    }

    @Update
    fun updateTask(task: MyTask)

    @Transaction
    suspend fun updateWithSyncFlag(task: MyTask) {
        updateTask(task.copy(isSynced = false))
    }

    @Transaction
    suspend fun syncAllTasks() {
        getAllTasksList().forEach { task ->
            updateTask(task.copy(isSynced = false))
        }
    }

    @Update
    suspend fun updateTasksByDataFromServer(tasks: List<MyTask>)
}
