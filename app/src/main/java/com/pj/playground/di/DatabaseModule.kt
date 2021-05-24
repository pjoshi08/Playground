package com.pj.playground.di

import android.content.Context
import androidx.room.Room
import com.pj.playground.data.AppDatabase
import com.pj.playground.data.LogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Modules are used to add bindings to [Hilt], or in other words, to tell [Hilt] how to provide
 * instances of different types. In [Hilt] modules, you can include bindings for types that
 * cannot be constructor-injected such as [interfaces] or [classes] that are not contained in
 * your project. An example of this is [OkHttpClient] - you need to use its builder to create
 * an instance.
 *
 * In Kotlin, modules that only contain @Provides functions can be object classes. This way,
 * [providers] get optimized and almost in-lined in generated code.
 *
 * @InstallIn tells Hilt the [containers] where the [bindings] are available by specifying a
 * Hilt [component].
 */
@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    /**
     * Each Hilt container comes with a set of default bindings that can be injected as
     * dependencies into your custom bindings. This is the case with [applicationContext].
     * To access it, you need to annotate the field with @ApplicationContext.
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase =
        Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "logging.db"
        ).build()

    @Provides
    fun provideLogDao(database: AppDatabase): LogDao = database.logDao()
}