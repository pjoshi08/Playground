package com.pj.playground.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkInfo
import com.bumptech.glide.Glide
import com.pj.playground.R
import com.pj.playground.util.KEY_IMAGE_URI
import kotlinx.android.synthetic.main.activity_blur.*

class BlurActivity : AppCompatActivity() {

    private lateinit var blurViewModel: BlurViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blur)

        blurViewModel = ViewModelProvider(this).get(BlurViewModel::class.java)

        // Image uri should be stored in the ViewModel; put it there then display
        val imageUriExtra = intent.getStringExtra(KEY_IMAGE_URI)
        blurViewModel.setImageUri(imageUriExtra)
        blurViewModel.imageUri?.let {
            Glide.with(this).load(it).into(image_view)
        }

        setOnClickListeners()
        blurViewModel.outputWorkInfos.observe(this, workInfosObserver())
    }

    private fun workInfosObserver(): Observer<List<WorkInfo>> {
        return Observer { listOfWorkInfo ->
            // Note that these next few lines grab a single WorkInfo if it exists
            // This code could be in a Transformation in the ViewModel; they are included here
            // so that the entire process of displaying a WorkInfo is in one location.

            // If there are no matching work info, do nothing
            if (listOfWorkInfo.isNullOrEmpty()) {
                return@Observer
            }

            // We only care about the one output status.
            // Every continuation has only one worker tagged TAG_OUTPUT
            val workInfo = listOfWorkInfo[0]

            if (workInfo.state.isFinished) {
                showWorkFinished()

                // Normally this processing, which is not directly related to drawing views on
                // screen would be in the ViewModel. For simplicity we are keeping it here.
                val outputImageUri = workInfo.outputData.getString(KEY_IMAGE_URI)

                // If there is an output file show "See File" button
                if (!outputImageUri.isNullOrEmpty()) {
                    blurViewModel.setOutputUri(outputImageUri)
                    see_file_button.visibility = View.VISIBLE
                }
            } else {
                showWorkInProgress()
            }
        }
    }

    private fun setOnClickListeners() {
        go_button.setOnClickListener {
            blurViewModel.applyBlur(blurLevel)
        }

        see_file_button.setOnClickListener {
            blurViewModel.outputUri?.let { currentUri ->
                val actionView = Intent(Intent.ACTION_VIEW, currentUri)
                actionView.resolveActivity(packageManager)?.let {
                    startActivity(actionView)
                }
            }
        }

        cancel_button.setOnClickListener {
            blurViewModel.cancelWork()
        }
    }

    private fun showWorkInProgress() {
        progress_bar.visibility = View.VISIBLE
        cancel_button.visibility = View.VISIBLE
        go_button.visibility = View.GONE
        see_file_button.visibility = View.GONE
    }

    private fun showWorkFinished() {
        progress_bar.visibility = View.GONE
        cancel_button.visibility = View.GONE
        go_button.visibility = View.VISIBLE
    }

    private val blurLevel: Int
        get() = when (radio_blur_group.checkedRadioButtonId) {
            R.id.radio_blur_lv_1 -> 1
            R.id.radio_blur_lv_2 -> 2
            R.id.radio_blur_lv_3 -> 3
            else -> 1
        }
}
