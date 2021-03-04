package com.pj.playground.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.appbar.AppBarLayout
import com.pj.playground.R
import kotlinx.android.synthetic.main.activity_step8.*

class Step8Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step8)

        coordinateMotion()
    }

    private fun coordinateMotion() {
        // TODO: set progress of MotionLayout based on an AppBarLayout.OnOffsetChangedListener

        val listener = AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            val seekPosition = -verticalOffset / appbar_layout.totalScrollRange.toFloat()
            motion_layout.progress = seekPosition
        }

        appbar_layout.addOnOffsetChangedListener(listener)
    }
}