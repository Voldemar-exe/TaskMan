package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import androidx.room.Update
import com.example.database.model.GroupTaskCrossRef
import com.example.database.model.GroupWithTasks
import com.example.database.model.MyTask
import com.example.database.model.TaskGroup

@Dao
interface SyncDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: TaskGroup): Long

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
            insertCrossRef(GroupTaskCrossRef(group.localId, task.localId))
        }
    }

    @Transaction
    suspend fun updateGroupsWithTaskFromServer(groups: List<GroupWithTasks>) {
        groups.forEach { groupWithTasks ->
            updateGroupWithTasks(groupWithTasks)
        }
    }

    @Update
    suspend fun updateTasksByDataFromServer(tasks: List<MyTask>)

}