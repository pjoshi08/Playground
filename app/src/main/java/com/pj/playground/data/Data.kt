package com.pj.playground.data

import android.app.Activity
import com.pj.playground.view.activity.*
import kotlin.reflect.KClass

data class Step(
    val number: String,
    val name: String,
    val caption: String,
    val activity: KClass<out Activity>,
    val highlight: Boolean = false
)

val data = listOf(
    Step(
        "Step 1",
        "Animations with Motion Layout",
        "Learn how to build a basic animation with Motion Layout. This will crash until you complete the step in the codelab.",
        Step1Activity::class
    ),
    Step(
        "Step 2",
        "Animating based on drag events",
        "Learn how to control animations with drag events. This will not display any animation until you complete the step in the codelab.",
        Step2Activity::class
    ),
    Step(
        "Step 3",
        "Modifying a path",
        "Learn how to use KeyFrames to modify a path between start and end.",
        Step3Activity::class
    ),
    Step(
        "Step 4",
        "Building complex paths",
        "Learn how to use KeyFrames to build complex paths through multiple KeyFrames.",
        Step4Activity::class
    ),
    Step(
        "Step 5",
        "Changing attributes with motion",
        "Learn how to resize and rotate views during animations.",
        Step5Activity::class
    ),
    Step(
        "Step 6",
        "Changing custom attributes",
        "Learn how to change custom attributes during motion.",
        Step6Activity::class
    ),
    Step(
        "Step 7",
        "OnSwipe with complex paths",
        "Learn how to control motion through complex paths with OnSwipe.",
        Step7Activity::class
    ),
    Step(
        "Completed: Steps 2-7",
        "Steps 2-7 completed",
        "All changes in steps 2-7 applied",
        Step7CompletedActivity::class,
        highlight = true
    ),
    Step(
        "Step 8",
        "Running motion with code",
        "Learn how to use MotionLayout to build complex collapsing toolbar animations.",
        Step8Activity::class
    ),
    Step(
        "Completed: Step 8 ",
        "Implements running motion with code",
        "Changes applied from step 8",
        Step8CompletedActivity::class,
        highlight = true
    )
)