package com.example.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.database.dao.GroupDao
import com.example.database.dao.SyncDao
import com.example.database.dao.TaskDao
import com.example.database.model.GroupTaskCrossRef
import com.example.database.model.MyTask
import com.example.database.model.TaskGroup
import com.example.shared.LocalDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Database(
    entities = [
        MyTask::class,
        TaskGroup::class,
        GroupTaskCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(ItemIconTypeConverter::class)
abstract class TaskManDatabase : RoomDatabase(), LocalDatabase {
    abstract fun taskDao(): TaskDao
    abstract fun groupDao(): GroupDao
    abstract fun syncDao(): SyncDao

    companion object {
        @Volatile
        private var INSTANCE: TaskManDatabase? = null

        fun getInstance(context: Context): TaskManDatabase =
            INSTANCE ?: synchronized(this) {
                val instance =
                    Room
                        .databaseBuilder(
                            context.applicationContext,
                            TaskManDatabase::class.java,
                            "task-man.db",
                        ).build()
                INSTANCE = instance
                instance
            }
    }

    override suspend fun clearDb() = withContext(Dispatchers.IO) {
        this@TaskManDatabase.clearAllTables()
    }
}
