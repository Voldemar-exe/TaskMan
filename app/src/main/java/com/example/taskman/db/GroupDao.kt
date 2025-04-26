package com.example.taskman.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.taskman.model.TaskGroup
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {

    @Transaction
    @Query("SELECT * FROM 'groups' ORDER BY name")
    fun getAllGroups(): Flow<List<TaskGroup>>

    @Query("DELETE FROM group_task WHERE groupId = :groupId")
    suspend fun deleteAllCrossRefsForGroup(groupId: Int)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupTaskCrossRef(crossRef: GroupTaskCrossRef)

    @Transaction
    @Query("SELECT * FROM 'groups' ORDER BY name")
    fun getAllGroupsWithTasks(): Flow<List<GroupWithTasks>>

    @Transaction
    @Query("SELECT * FROM 'groups' WHERE groupId = :groupId")
    suspend fun getGroupById(groupId: Int): GroupWithTasks?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: TaskGroup): Long

    @Update
    suspend fun updateGroup(group: TaskGroup)

    @Delete
    suspend fun deleteGroup(group: TaskGroup)
}