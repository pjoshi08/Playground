package com.pj.playground.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pj.playground.data.Task

/**
 * The Room Database that contains the Task table.
 *
 * Note that exportSchema should be true in production databases.
 */
@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class ToDoDatabase : RoomDatabase() {

    abstract fun taskDao(): TasksDao
}