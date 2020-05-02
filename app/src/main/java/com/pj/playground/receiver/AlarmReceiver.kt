package com.pj.playground.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.pj.playground.R

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // TODO: Step 1.10 [Optional] remove toast
        Toast.makeText(context, R.string.eggs_ready, Toast.LENGTH_SHORT).show()

        // TODO: Step 1.9 add call to sendNotification
    }
}