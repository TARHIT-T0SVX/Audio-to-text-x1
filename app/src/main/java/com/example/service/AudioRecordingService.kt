package com.example.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.TranscribeApplication
import com.example.audio.AudioRecorderManager
import java.io.File

class AudioRecordingService : Service() {

    private val binder = LocalBinder()
    private lateinit var recorderManager: AudioRecorderManager
    var isRecording = false
        private set
    var isPaused = false
        private set

    var currentAudioFile: File? = null
        private set

    inner class LocalBinder : Binder() {
        fun getService(): AudioRecordingService = this@AudioRecordingService
    }

    override fun onCreate() {
        super.onCreate()
        recorderManager = AudioRecorderManager(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startRecordingService()
            ACTION_PAUSE -> pauseRecordingService()
            ACTION_RESUME -> resumeRecordingService()
            ACTION_STOP -> stopRecordingService()
        }
        return START_STICKY
    }

    private fun startRecordingService() {
        if (!isRecording) {
            currentAudioFile = recorderManager.startRecording()
            isRecording = true
            isPaused = false
            startForeground(NOTIFICATION_ID, buildNotification("Recording audio in background..."))
        }
    }

    private fun pauseRecordingService() {
        if (isRecording && !isPaused) {
            recorderManager.pauseRecording()
            isPaused = true
            updateNotification("Recording paused")
        }
    }

    private fun resumeRecordingService() {
        if (isRecording && isPaused) {
            recorderManager.resumeRecording()
            isPaused = false
            updateNotification("Recording audio in background...")
        }
    }

    fun stopRecordingService(): File? {
        val file = recorderManager.stopRecording()
        isRecording = false
        isPaused = false
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        return file
    }

    fun getMaxAmplitude(): Int = recorderManager.getMaxAmplitude()

    fun getDurationFormatted(): String = recorderManager.getRecordingDurationFormatted()

    private fun buildNotification(contentText: String): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingOpenIntent = PendingIntent.getActivity(
            this, 0, openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, AudioRecordingService::class.java).apply {
            action = ACTION_STOP
        }
        val pendingStopIntent = PendingIntent.getService(
            this, 1, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, TranscribeApplication.RECORDING_CHANNEL_ID)
            .setContentTitle("Transcribe Background Recording")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingOpenIntent)
            .setOngoing(true)
            .addAction(R.drawable.ic_launcher_foreground, "Stop", pendingStopIntent)
            .build()
    }

    private fun updateNotification(contentText: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.notify(NOTIFICATION_ID, buildNotification(contentText))
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onDestroy() {
        recorderManager.release()
        super.onDestroy()
    }

    companion object {
        const val NOTIFICATION_ID = 1001
        const val ACTION_START = "com.example.service.START_RECORDING"
        const val ACTION_PAUSE = "com.example.service.PAUSE_RECORDING"
        const val ACTION_RESUME = "com.example.service.RESUME_RECORDING"
        const val ACTION_STOP = "com.example.service.STOP_RECORDING"
    }
}
