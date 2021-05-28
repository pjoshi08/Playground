package com.pj.playground.di

import com.pj.playground.data.LoggerDataSource
import com.pj.playground.data.LoggerInMemoryDataSource
import com.pj.playground.data.LoggerLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton


/**
 * We need to define a [qualifier per implementation] since each qualifier will be
 * used to identify a binding. When injecting the type in an Android class or
 * having that type as a dependency of other classes, the qualifier annotation
 * needs to be used to avoid ambiguity.
 */
@Qualifier
annotation class DatabaseLogger

@Qualifier
annotation class InMemoryLogger

/**
 * As the @[DatabaseLogger] qualifier is installed in [SingletonComponent], it could
 * be injected into the [LogApplication] class. However, as @[InMemoryLogger] is
 * installed in [ActivityComponent], it cannot be injected into the [LogApplication]
 * class because the [application container] doesn't know about that binding.
 */
@InstallIn(SingletonComponent::class)
@Module
abstract class LoggingDatabaseModule {

    /**
     * @[Binds] methods must have the scoping annotations [if the type is scoped], so
     * that's why the functions are annotated with @[Singleton] and @[ActivityScoped].
     *
     * If @[Binds] or @[Provides] are used as a [binding] for a [type], the scoping
     * annotations in the type are not used anymore, so you can go ahead and remove
     * them from the different implementation classes.
     */
    @DatabaseLogger
    @Singleton
    @Binds
    abstract fun bindDatabaseLogger(impl: LoggerLocalDataSource): LoggerDataSource
}

@InstallIn(ActivityComponent::class)
@Module
abstract class LoggingInMemoryModule {

    @InMemoryLogger
    @ActivityScoped
    @Binds
    abstract fun bindInMemoryLogger(impl: LoggerInMemoryDataSource): LoggerDataSource
}
