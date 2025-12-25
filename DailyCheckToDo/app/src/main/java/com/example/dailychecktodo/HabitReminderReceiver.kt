package com.example.dailychecktodo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class HabitReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val habitName = intent.getStringExtra("HABIT_NAME") ?: "Time to check your habit!"
        val habitId = intent.getIntExtra("HABIT_ID", 1) // Get the unique ID for the notification

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "HABIT_REMINDERS",
                "Habit Reminders",
                NotificationManager.IMPORTANCE_HIGH // Use HIGH importance to make it pop up
            ).apply {
                description = "Channel for habit reminder notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification with a standard system icon and high priority
        val notification = NotificationCompat.Builder(context, "HABIT_REMINDERS")
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Use a guaranteed-valid system icon
            .setContentTitle("Habit Reminder")
            .setContentText(habitName)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Set priority to HIGH
            .setAutoCancel(true)
            .build()

        // Show the notification with a unique ID
        notificationManager.notify(habitId, notification)
    }
}
