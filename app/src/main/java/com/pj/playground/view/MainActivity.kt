package com.pj.playground.view

import android.animation.*
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import com.pj.playground.R
import com.pj.playground.util.disableViewDuringAnimation
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setClickListeners()
    }

    private fun setClickListeners() {
        rotateButton.setOnClickListener { rotater() }
        translateButton.setOnClickListener { translater() }
        scaleButton.setOnClickListener { scaler() }
        fadeButton.setOnClickListener { fader() }
        colorizeButton.setOnClickListener { colorzier() }
        showerButton.setOnClickListener { shower() }
    }

    private fun shower() {
        val container = star.parent as ViewGroup
        val containerW = container.width
        val containerH = container.height
        var starW = star.width.toFloat()
        var starH = star.height.toFloat()

        val newStar = AppCompatImageView(this)
        newStar.setImageResource(R.drawable.ic_star)
        newStar.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        container.addView(newStar)

        newStar.scaleX = Math.random().toFloat() * 1.5f + .1f
        newStar.scaleY = newStar.scaleX
        starW *= newStar.scaleX
        starH *= newStar.scaleY

        newStar.translationX = Math.random().toFloat() *
                containerW - starW / 2

        val mover = ObjectAnimator.ofFloat(
            newStar, View.TRANSLATION_Y,
            -starH, containerH + starH
        )
        mover.interpolator = AccelerateInterpolator(1f)
        val rotator = ObjectAnimator.ofFloat(
            newStar, View.ROTATION,
            (Math.random() * 1080).toFloat()
        )
        rotator.interpolator = LinearInterpolator()

        with(AnimatorSet()) {
            playTogether(mover, rotator)
            duration = (Math.random() * 1500 + 500).toLong()

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    container.removeView(newStar)
                }
            })

            start()
        }
    }

    private fun colorzier() {
        //val animator = ObjectAnimator.ofInt(star.parent, "backgroundColor", Color.BLACK, Color.RED).start()
        val animator = ObjectAnimator.ofArgb(
            star.parent,
            "backgroundColor", Color.BLACK, Color.RED
        )
        with(animator) {
            duration = 500
            repeatCount = 1
            repeatMode = ObjectAnimator.REVERSE
            disableViewDuringAnimation(colorizeButton)
            start()
        }
    }

    private fun fader() {
        val animator = ObjectAnimator.ofFloat(star, View.ALPHA, 0f)
        with(animator) {
            repeatCount = 1
            repeatMode = ObjectAnimator.REVERSE
            disableViewDuringAnimation(fadeButton)
            start()
        }
    }

    private fun scaler() {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 6f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 6f)
        val animator = ObjectAnimator.ofPropertyValuesHolder(star, scaleX, scaleY)
        with(animator) {
            duration = 1000
            disableViewDuringAnimation(scaleButton)
            repeatCount = 1
            repeatMode = ObjectAnimator.REVERSE
            start()
        }
    }

    private fun rotater() {
        val animator = ObjectAnimator.ofFloat(star, View.ROTATION, -360f, 0f)
        with(animator) {
            duration = 1000
            disableViewDuringAnimation(rotateButton)
            start()
        }
    }

    private fun translater() {
        val animator = ObjectAnimator.ofFloat(star, View.TRANSLATION_X, 200f)
        with(animator) {
            repeatCount = 1
            repeatMode = ObjectAnimator.REVERSE
            disableViewDuringAnimation(translateButton)
            start()
        }
    }
}
