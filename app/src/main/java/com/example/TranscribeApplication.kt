package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.data.local.AppDatabase

class TranscribeApplication : Application() {

    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                RECORDING_CHANNEL_ID,
                "Background Audio Recording",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows active background audio recording status and controls"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    companion object {
        const val RECORDING_CHANNEL_ID = "recording_channel"
    }
}
