package com.example.taskman.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.taskman.model.MyTask
import com.example.taskman.model.TaskGroup
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
abstract class TaskManDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun groupDao(): GroupDao

    suspend fun clearAll() = withContext(Dispatchers.IO) {
        clearAllTables()
    }

    companion object {
        @Volatile
        private var INSTANCE: TaskManDatabase? = null

        fun getInstance(context: Context): TaskManDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskManDatabase::class.java,
                    "task-man.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}