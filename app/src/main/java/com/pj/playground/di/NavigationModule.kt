package com.pj.playground.di

import com.pj.playground.navigator.AppNavigator
import com.pj.playground.navigator.AppNavigatorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

/**
 * For an [interface], we cannot use [constructor] injection. To tell [Hilt] what implementation
 * to use for an interface, you can use the @Binds [annotation] on a function inside a Hilt
 * module.
 *
 * Hilt Modules cannot contain both non-static and abstract binding methods, so you cannot place
 * @Binds and @Provides annotations in the same class.
 */
@InstallIn(ActivityComponent::class)
@Module
abstract class NavigationModule {

    /**
     * @Binds must annotate an [abstract] function (since it's abstract, it doesn't contain
     * any code and the [class] needs to be abstract too).
     *
     * @param [AppNavigatorImpl]: The implementation is specified by adding a unique [parameter]
     * with the interface implementation type
     *
     * @return [AppNavigator]: The return type of the abstract function is the interface
     * we want to provide an implementation for.
     */
    @Binds
    abstract fun bindNavigator(impl: AppNavigatorImpl): AppNavigator
}