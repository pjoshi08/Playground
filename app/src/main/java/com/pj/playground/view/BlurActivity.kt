package com.pj.playground.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
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
