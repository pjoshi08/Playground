package com.pj.playground.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pj.playground.R
import com.pj.playground.data.data
import com.pj.playground.view.adapter.MainAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupAdapter()
    }

    private fun setupAdapter() {
        recycler_view.adapter = MainAdapter(data)
    }
}
