package com.pj.playground.data.source

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.pj.playground.data.Result
import com.pj.playground.data.Result.Success
import com.pj.playground.data.Result.Error
import com.pj.playground.data.Task
import com.pj.playground.data.source.local.TasksLocalDataSource
import com.pj.playground.data.source.local.ToDoDatabase
import com.pj.playground.data.source.remote.TasksRemoteDataSource
import kotlinx.coroutines.*

/**
 * Concrete implementation to load tasks from the data sources into a cache.
 */
class DefaultTasksRepository private constructor(application: Application) {

    private val tasksRemoteDataSource: TasksDataSource
    private val tasksLocalDataSource: TasksDataSource
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    companion object {
        @Volatile
        private var INSTANCE: DefaultTasksRepository? = null

        fun getRepository(app: Application): DefaultTasksRepository {
            return INSTANCE ?: synchronized(this) {
                DefaultTasksRepository(app).also { INSTANCE = it }
            }
        }
    }

    init {
        val database = Room.databaseBuilder(
            application.applicationContext,
            ToDoDatabase::class.java, "Tasks.db"
        )
            .build()

        tasksRemoteDataSource = TasksRemoteDataSource
        tasksLocalDataSource = TasksLocalDataSource(database.taskDao())
    }

    suspend fun getTasks(forceUpdate: Boolean = false): Result<List<Task>> {
        if (forceUpdate) {
            try {
                updateTasksFromRemoteDataSource()
            } catch (e: Exception) {
                return Error(e)
            }
        }

        return tasksLocalDataSource.getTasks()
    }

    suspend fun refreshTasks() {
        updateTasksFromRemoteDataSource()
    }

    fun observeTasks(): LiveData<Result<List<Task>>> = tasksLocalDataSource.observeTasks()

    suspend fun refreshTask(taskId: String) {
        updateTasksFromRemoteDataSource(taskId)
    }

    fun observeTask(taskId: String): LiveData<Result<Task>> =
        tasksLocalDataSource.observeTask(taskId)

    /**
     * Relies on [getTasks] to fetch data and picks the task with the same ID.
     */
    suspend fun getTask(taskId: String, forceUpdate: Boolean = false): Result<Task> {
        if (forceUpdate) {
            updateTasksFromRemoteDataSource(taskId)
        }
        return tasksLocalDataSource.getTask(taskId)
    }

    suspend fun saveTask(task: Task) {
        coroutineScope {
            launch { tasksLocalDataSource.saveTask(task) }
            launch { tasksRemoteDataSource.saveTask(task) }
        }
    }

    suspend fun completeTask(task: Task) {
        coroutineScope {
            launch { tasksLocalDataSource.completeTask(task) }
            launch { tasksRemoteDataSource.completeTask(task) }
        }
    }

    suspend fun completeTask(taskId: String) {
        withContext(ioDispatcher) {
            (getTaskWithId(taskId) as? Success)?.let { task -> completeTask(task.data) }
        }
    }

    suspend fun activateTask(task: Task) = withContext<Unit>(ioDispatcher) {
        launch { tasksLocalDataSource.activateTask(task) }
        launch { tasksRemoteDataSource.activateTask(task) }
    }

    suspend fun activateTask(taskId: String) {
        withContext(ioDispatcher) {
            (getTaskWithId(taskId) as? Success)?.let { task -> activateTask(task.data) }
        }
    }

    suspend fun clearCompletedTasks() {
        coroutineScope {
            launch { tasksLocalDataSource.clearCompletedTasks() }
            launch { tasksRemoteDataSource.clearCompletedTasks() }
        }
    }

    suspend fun deleteAllTasks() {
        withContext(ioDispatcher) {
            launch { tasksLocalDataSource.deleteAllTasks() }
            launch { tasksRemoteDataSource.deleteAllTasks() }
        }
    }

    suspend fun deleteTask(taskId: String) {
        coroutineScope {
            launch { tasksRemoteDataSource.deleteTask(taskId) }
            launch { tasksLocalDataSource.deleteTask(taskId) }
        }
    }

    private suspend fun updateTasksFromRemoteDataSource() {
        val remoteTasks = tasksRemoteDataSource.getTasks()

        if (remoteTasks is Success) {
            // Real apps might want to do a proper sync.
            tasksLocalDataSource.deleteAllTasks()
            remoteTasks.data.forEach { task -> tasksLocalDataSource.saveTask(task) }
        } else if (remoteTasks is Error) {
            throw remoteTasks.exception
        }
    }

    private suspend fun updateTasksFromRemoteDataSource(taskId: String) {
        val remoteTask = tasksRemoteDataSource.getTask(taskId)

        if (remoteTask is Success) {
            tasksLocalDataSource.saveTask(remoteTask.data)
        }
    }

    private suspend fun getTaskWithId(taskId: String): Result<Task> =
        tasksLocalDataSource.getTask(taskId)
}