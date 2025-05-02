package com.example.taskman.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.Relation
import com.example.taskman.model.MyTask
import com.example.taskman.model.TaskGroup

@Entity(
    tableName = "group_task",
    primaryKeys = ["groupId", "taskId"],
    indices = [
        Index("groupId"),
        Index("taskId")
    ],
    foreignKeys = [
        ForeignKey(
            entity = TaskGroup::class,
            parentColumns = ["groupId"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MyTask::class,
            parentColumns = ["taskId"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class GroupTaskCrossRef(
    val groupId: Int,
    val taskId: Int
)

data class GroupWithTasks(
    @Embedded val group: TaskGroup,
    @Relation(
        parentColumn = "groupId",
        entityColumn = "taskId",
        associateBy = Junction(GroupTaskCrossRef::class)
    )
    val tasks: List<MyTask>
)