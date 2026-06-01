package com.mavi.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.mavi.MainActivity
import com.mavi.R

class CallNotificationManager(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_ID = "call_channel"
        const val INCOMING_CALL_NOTIFICATION_ID = 1001
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Incoming Calls",
                NotificationManager.IMPORTANCE_MAX
            ).apply {
                description = "Notifications for incoming calls"
                enableVibration(true)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showIncomingCallNotification(
        callId: String,
        callerName: String,
        callerId: Int
    ) {
        val acceptIntent = Intent(context, CallReceiver::class.java).apply {
            action = "ACCEPT_CALL"
            putExtra("call_id", callId)
            putExtra("caller_name", callerName)
            putExtra("caller_id", callerId)
        }

        val rejectIntent = Intent(context, CallReceiver::class.java).apply {
            action = "REJECT_CALL"
            putExtra("call_id", callId)
        }

        val acceptPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            acceptIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val rejectPendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            rejectIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val mainIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("call_id", callId)
            putExtra("caller_name", callerName)
            putExtra("caller_id", callerId)
            putExtra("open_call_screen", true)
        }

        val mainPendingIntent = PendingIntent.getActivity(
            context,
            2,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_call_icon)
            .setContentTitle("Incoming Call")
            .setContentText("$callerName is calling...")
            .setContentIntent(mainPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setFullScreenIntent(mainPendingIntent, true)
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_accept_call,
                "Accept",
                acceptPendingIntent
            )
            .addAction(
                R.drawable.ic_reject_call,
                "Reject",
                rejectPendingIntent
            )
            .setVibrate(longArrayOf(0, 500, 500, 500))
            .build()

        notification.flags = notification.flags or NotificationCompat.FLAG_INSISTENT

        notificationManager.notify(INCOMING_CALL_NOTIFICATION_ID, notification)
    }

    fun dismissIncomingCallNotification() {
        notificationManager.cancel(INCOMING_CALL_NOTIFICATION_ID)
    }
}
