package com.pj.playground

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.pj.playground.view.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Running a test that uses [Hilt]:
 * 1. Be annotated with @[HiltAndroidTest] which is responsible for generating the
 *    [Hilt components] for each test
 * 2. Use @[HiltAndroidRule]
 */
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class AppTest {

    /**
     * Use the [HiltAndroidRule] that manages the components' [state] and is used to
     * perform [injection] on your test.
     */
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    // This method can be removed. Keeping this method for visibility and knowledge
    @After
    fun tearDown() {
        // Remove Logs after the test finishes
        /*ServiceLocator(InstrumentationRegistry.getInstrumentation().targetContext)
            .loggerLocalDataSource.removeLogs()*/
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.pj.playground", appContext.packageName)
    }

    @Test
    fun happyPath() {
        ActivityScenario.launch(MainActivity::class.java)

        // Check Buttons Fragment screen is displayed
        onView(withId(R.id.textView)).check(matches(isDisplayed()))

        // Tap on Button 1
        onView(withId(R.id.button1)).perform(click())

        // Navigate to Logs Screen
        onView(withId(R.id.all_logs)).perform(click())

        // Check Logs Fragment screen is displayed
        onView(withText(containsString("Interaction with 'Button 1'")))
            .check(matches(isDisplayed()))
    }
}
