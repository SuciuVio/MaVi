package com.mavi.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class CallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val callId = intent.getStringExtra("call_id") ?: return
        val callerName = intent.getStringExtra("caller_name") ?: "Unknown"
        val callerId = intent.getIntExtra("caller_id", 0)

        when (intent.action) {
            "ACCEPT_CALL" -> {
                Log.d("CallReceiver", "Accepting call from $callerName")
                val mainIntent = Intent(context, android.app.Activity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra("call_id", callId)
                    putExtra("caller_name", callerName)
                    putExtra("caller_id", callerId)
                    putExtra("accept_call", true)
                }
                // context.startActivity(mainIntent) // Will be handled by MainActivity
            }
            "REJECT_CALL" -> {
                Log.d("CallReceiver", "Rejecting call from $callerName")
                // Implement rejection logic
            }
        }
    }
}
