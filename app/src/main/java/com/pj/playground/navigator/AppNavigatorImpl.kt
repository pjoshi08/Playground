package com.pj.playground.navigator

import androidx.fragment.app.FragmentActivity
import com.pj.playground.R
import com.pj.playground.view.ButtonsFragment
import com.pj.playground.view.LogsFragment

/**
 * Navigator implementation.
 */
class AppNavigatorImpl(private val activity: FragmentActivity) : AppNavigator {

    override fun navigateTo(screens: Screens) {
        val fragment = when (screens) {
            Screens.BUTTONS -> ButtonsFragment()
            Screens.LOGS -> LogsFragment()
        }

        activity.supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment)
            .addToBackStack(fragment::class.java.canonicalName)
            .commit()
    }
}