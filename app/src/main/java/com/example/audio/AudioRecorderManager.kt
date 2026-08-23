package com.example.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.IOException

class AudioRecorderManager(private val context: Context) {

    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null
    private var startTimeMs: Long = 0L
    private var isRecording = false
    private var isPaused = false
    private var pausedDurationMs: Long = 0L
    private var pauseStartMs: Long = 0L

    fun startRecording(): File? {
        stopRecording()
        val audioDir = File(context.cacheDir, "audio_recordings").apply { mkdirs() }
        val file = File(audioDir, "rec_${System.currentTimeMillis()}.m4a")

        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioSamplingRate(44100)
            setAudioEncodingBitRate(128000)
            setOutputFile(file.absolutePath)

            try {
                prepare()
                start()
                isRecording = true
                isPaused = false
                startTimeMs = System.currentTimeMillis()
                pausedDurationMs = 0L
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
        outputFile = file
        return file
    }

    fun pauseRecording() {
        if (isRecording && !isPaused && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                recorder?.pause()
                isPaused = true
                pauseStartMs = System.currentTimeMillis()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun resumeRecording() {
        if (isRecording && isPaused && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                recorder?.resume()
                isPaused = false
                pausedDurationMs += (System.currentTimeMillis() - pauseStartMs)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun stopRecording(): File? {
        if (!isRecording) return outputFile
        try {
            recorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            recorder = null
            isRecording = false
            isPaused = false
        }
        return outputFile
    }

    fun getMaxAmplitude(): Int {
        if (!isRecording || isPaused) return 0
        return try {
            recorder?.maxAmplitude ?: 0
        } catch (e: Exception) {
            0
        }
    }

    fun getRecordingDurationFormatted(): String {
        if (!isRecording) return "00:00"
        val now = System.currentTimeMillis()
        val effectiveMs = if (isPaused) {
            pauseStartMs - startTimeMs - pausedDurationMs
        } else {
            now - startTimeMs - pausedDurationMs
        }
        val totalSeconds = (effectiveMs / 1000).coerceAtLeast(0)
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds)
    }

    fun getRecordingDurationSeconds(): Int {
        if (!isRecording) return 0
        val now = System.currentTimeMillis()
        val effectiveMs = if (isPaused) {
            pauseStartMs - startTimeMs - pausedDurationMs
        } else {
            now - startTimeMs - pausedDurationMs
        }
        return ((effectiveMs / 1000).coerceAtLeast(0)).toInt()
    }

    fun release() {
        stopRecording()
    }
}
