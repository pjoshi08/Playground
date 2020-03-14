package com.pj.playground.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.pj.playground.R
import com.pj.playground.data.TitleRepository
import com.pj.playground.data.getDatabase
import com.pj.playground.data.getNetworkService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get MainViewModel by passing a database to the factory
        val database = getDatabase(this)
        val respository = TitleRepository(getNetworkService(), database.titleDao)
        viewModel = ViewModelProvider(
            this,
            MainViewModel.FACTORY(respository)
        ).get(MainViewModel::class.java)

        // When rootLayout is clicked call onMainViewClicked in ViewModel
        rootLayout.setOnClickListener {
            viewModel.onMainViewClicked()
        }

        setupObservers()
    }

    private fun setupObservers() {
        // update the title when the [MainViewModel.title] changes
        viewModel.title.observe(this, Observer { value ->
            value?.let {
                tvTitle.text = it
            }
        })

        viewModel.title

        viewModel.taps.observe(this, Observer { value ->
            taps.text = value
        })

        // show the spinner when [MainViewModel.spinner] is true
        viewModel.spinner.observe(this, Observer { value ->
            value.let { show ->
                spinner.visibility = if (show) View.VISIBLE else View.GONE
            }
        })

        viewModel.snackBar.observe(this, Observer { text ->
            text?.let {
                Snackbar.make(rootLayout, text, Snackbar.LENGTH_LONG).show()
                viewModel.onSnackbarShown()
            }
        })
    }
}
