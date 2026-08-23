package com.example.stt

import android.content.Context
import kotlinx.coroutines.flow.Flow

interface SpeechRecognitionEngine {
    fun initialize(context: Context, languageCode: String)
    fun startLiveRecognition(onPartialResult: (String) -> Unit, onError: (String) -> Unit)
    fun stopLiveRecognition()
    suspend fun transcribeAudioFile(
        context: Context,
        audioPath: String,
        languageCode: String,
        onProgress: (Int) -> Unit
    ): String
}
