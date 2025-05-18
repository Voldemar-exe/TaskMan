package com.example.taskman.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.taskman.model.MyTask
import com.example.taskman.model.TaskGroup
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {

    @Transaction
    @Query("SELECT * FROM `groups` ORDER BY name")
    fun getAllGroups(): Flow<List<TaskGroup>>

    @Query("DELETE FROM group_task WHERE groupId = :groupId")
    suspend fun deleteAllCrossRefsForGroup(groupId: Int)

    @Query("DELETE FROM `groups` WHERE groupId = :groupId")
    suspend fun deleteGroupById(groupId: Int)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupTaskCrossRef(crossRef: GroupTaskCrossRef)

    @Transaction
    @Query("SELECT * FROM `groups`")
    fun getAllGroupsWithTasksFlow(): Flow<List<GroupWithTasks>>

    @Transaction
    @Query("SELECT * FROM `groups` ORDER BY name")
    fun getAllGroupsWithTasksList(): List<GroupWithTasks>

    @Transaction
    @Query("SELECT * FROM `groups` WHERE isSynced = 0")
    fun getAllNotSyncedList(): List<GroupWithTasks>

    @Transaction
    @Query("SELECT * FROM `groups` WHERE isSynced = 0")
    fun getAllNotSyncedFlow(): Flow<List<GroupWithTasks>>

    @Transaction
    @Query("SELECT * FROM `groups` WHERE groupId = :groupId")
    suspend fun getGroupById(groupId: Int): GroupWithTasks?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: TaskGroup): Long

    @Update
    suspend fun updateGroup(group: TaskGroup)

    @Update
    suspend fun updateGroups(groups: List<TaskGroup>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: MyTask)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRef(ref: GroupTaskCrossRef)

    @Transaction
    suspend fun updateGroupWithTasks(groupWithTasks: GroupWithTasks) {
        val group = groupWithTasks.group
        insertGroup(group)

        for (task in groupWithTasks.tasks) {
            insertTask(task)
            insertCrossRef(GroupTaskCrossRef(group.groupId, task.taskId))
        }
    }

    @Transaction
    suspend fun updateGroupsWithTaskFromServer(groups: List<GroupWithTasks>) {
        groups.forEach { groupWithTasks ->
            updateGroupWithTasks(groupWithTasks)
        }
    }

    @Delete
    suspend fun deleteGroup(group: TaskGroup)
}
