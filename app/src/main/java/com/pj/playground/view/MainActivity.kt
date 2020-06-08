package com.pj.playground.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pj.playground.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ClippedView(this))
    }
}
