package com.pj.playground.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus.*
import com.pj.playground.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var splitInstalManager: SplitInstallManager
    lateinit var request: SplitInstallRequest
    val DYNAMIC_FEATURE = "news_feature"

    var sessionId = 0
    private val listener = SplitInstallStateUpdatedListener {
        sessionId = it.sessionId()

        when (it.status()) {
            DOWNLOADED -> {
                val totalBytes = it.totalBytesToDownload()
                val progress = it.bytesDownloaded()
                Log.v("Status", "Total: $totalBytes, Progress: $progress")
            }

            INSTALLING -> Log.v("Status", "Installing")
            INSTALLED -> Log.v("Status", "Installed")
            FAILED -> Log.v("Status", "Failed")
            REQUIRES_USER_CONFIRMATION -> Log.v("Status", "REQUIRES_USER_CONFIRMATION")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initDynamicModules()
        setClickListeners()
    }

    private fun initDynamicModules() {
        splitInstalManager = SplitInstallManagerFactory.create(this)

        request = SplitInstallRequest
            .newBuilder()
            .addModule(DYNAMIC_FEATURE)
            .build()
    }

    private fun setClickListeners() {
        buttonClick.setOnClickListener {
            if (!isDynamicFeatureInstalled(DYNAMIC_FEATURE)) {
                downloadFeature()
            } else {
                buttonOpenNewsModule.visibility = View.VISIBLE
                buttonDeleteNewsModule.visibility = View.VISIBLE
            }
        }

        buttonOpenNewsModule.setOnClickListener {
            val intent = Intent()
                .setClassName(
                    this,
                    "com.example.news_feature.newsloader.view.NewsLoaderActivity"
                )
            startActivity(intent)
        }

        buttonDeleteNewsModule.setOnClickListener {
            val modulesToRemove = ArrayList<String>()
            modulesToRemove.add(DYNAMIC_FEATURE)
            uninstallDynamicFeature(modulesToRemove)
        }
    }

    private fun uninstallDynamicFeature(modulesToRemove: ArrayList<String>) {
        splitInstalManager
            .deferredUninstall(modulesToRemove)
            .addOnCompleteListener {
                buttonOpenNewsModule.visibility = View.GONE
                buttonDeleteNewsModule.visibility = View.GONE
            }
    }

    private fun isDynamicFeatureInstalled(dynamicFeature: String): Boolean =
        splitInstalManager.installedModules.contains(dynamicFeature)

    private fun downloadFeature() {

        splitInstalManager.startInstall(request)
            .addOnCompleteListener { }
            .addOnFailureListener { }
            .addOnSuccessListener {
                buttonOpenNewsModule.visibility = View.VISIBLE
                buttonDeleteNewsModule.visibility = View.VISIBLE
            }
    }

    override fun onResume() {
        splitInstalManager.registerListener(listener)
        super.onResume()
    }

    override fun onPause() {
        splitInstalManager.unregisterListener(listener)
        super.onPause()
    }
}
