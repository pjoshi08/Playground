package com.pj.playground.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import com.pj.playground.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val canvasView = MyCanvasView(this)
        canvasView.systemUiVisibility = SYSTEM_UI_FLAG_FULLSCREEN
        canvasView.contentDescription = getString(R.string.canvasContentDescription)

        setContentView(canvasView)
    }
}
