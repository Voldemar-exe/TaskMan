package com.example.database.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.Relation
import com.example.shared.UserGroupWithTasks

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
            parentColumns = ["localId"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MyTask::class,
            parentColumns = ["localId"],
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
    @Embedded
    override val group: TaskGroup,
    @Relation(
        parentColumn = "localId",
        entityColumn = "localId",
        associateBy = Junction(
            value = GroupTaskCrossRef::class,
            parentColumn = "groupId",
            entityColumn = "taskId"
        )
    )
    override val tasks: List<MyTask>
) : UserGroupWithTasks()