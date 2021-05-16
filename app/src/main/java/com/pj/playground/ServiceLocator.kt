package com.pj.playground

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.room.Room
import com.pj.playground.data.AppDatabase
import com.pj.playground.data.LoggerLocalDataSource
import com.pj.playground.navigator.AppNavigator
import com.pj.playground.navigator.AppNavigatorImpl
import com.pj.playground.util.DateFormatter

class ServiceLocator(applicationContext: Context) {
    private val logsDatabase = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java,
        "logging.db"
    ).build()

    val loggerLocalDataSource = LoggerLocalDataSource(logsDatabase.logDao())

    fun provideDateFormatter() = DateFormatter()

    fun provideNavigator(activity: FragmentActivity): AppNavigator = AppNavigatorImpl(activity)
}