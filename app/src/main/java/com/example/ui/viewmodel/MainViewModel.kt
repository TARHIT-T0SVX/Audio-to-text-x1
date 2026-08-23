package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.TranscribeApplication
import com.example.audio.AudioImportManager
import com.example.audio.ImportedAudioFile
import com.example.data.local.LanguageModelEntity
import com.example.data.local.TranscriptEntity
import com.example.data.preferences.AppThemeMode
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.repository.ModelRepository
import com.example.data.repository.TranscriptRepository
import com.example.export.ExportManager
import com.example.stt.OfflineSpeechEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = (application as TranscribeApplication).database
    private val transcriptRepository = TranscriptRepository(database.transcriptDao())
    private val modelRepository = ModelRepository(database.languageModelDao())
    private val preferencesRepository = UserPreferencesRepository(application)
    private val speechEngine = OfflineSpeechEngine()

    // Navigation state
    val selectedTab = MutableStateFlow(0) // 0: Home, 1: History, 2: Models, 3: Settings
    val currentSubScreen = MutableStateFlow<String?>(null) // "IMPORT", "PROCESSING", "DETAIL"

    // Preferences & Theme
    val userPreferences = preferencesRepository.userPreferencesFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        com.example.data.preferences.UserPreferences()
    )

    // History & Search
    val searchQuery = MutableStateFlow("")
    val transcripts: StateFlow<List<TranscriptEntity>> = searchQuery
        .combine(transcriptRepository.allTranscripts) { query, list ->
            if (query.isBlank()) list
            else list.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.content.contains(query, ignoreCase = true)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedTranscript = MutableStateFlow<TranscriptEntity?>(null)

    // Language Models
    val languageModels: StateFlow<List<LanguageModelEntity>> = modelRepository.allModels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Recording State
    val isRecording = MutableStateFlow(false)
    val isPaused = MutableStateFlow(false)
    val recordingTimeText = MutableStateFlow("00:00")
    val waveformAmplitudes = MutableStateFlow(List(18) { 0.2f })
    val liveTranscriptText = MutableStateFlow("")

    private var recordingTimerJob: Job? = null
    private var waveformJob: Job? = null
    private var secondsElapsed = 0

    // Imported Audio List
    val importedFiles = MutableStateFlow<List<ImportedAudioFile>>(
        listOf(
            ImportedAudioFile(
                uri = Uri.EMPTY,
                fileName = "Interview_Research_Final.mp3",
                sizeMB = "124 MB",
                durationText = "45:20",
                localFilePath = "",
                timestampText = "Today",
                isTranscribed = false
            ),
            ImportedAudioFile(
                uri = Uri.EMPTY,
                fileName = "Client_Meeting_Q3.m4a",
                sizeMB = "32 MB",
                durationText = "12:05",
                localFilePath = "",
                timestampText = "Yesterday",
                isTranscribed = false
            ),
            ImportedAudioFile(
                uri = Uri.EMPTY,
                fileName = "Lecture_Physics_101.wav",
                sizeMB = "85 MB",
                durationText = "58:10",
                localFilePath = "",
                timestampText = "May 05, 2024",
                isTranscribed = true
            )
        )
    )

    // Processing File State
    val isProcessing = MutableStateFlow(false)
    val processingProgress = MutableStateFlow(65)
    val processingFileName = MutableStateFlow("Interview_Research_Final.mp3")
    val processingModelName = MutableStateFlow("English (UK) - 140MB")
    private var processingJob: Job? = null

    // Export Sheet State
    val showExportSheet = MutableStateFlow(false)
    val exportTargetTranscript = MutableStateFlow<TranscriptEntity?>(null)
    val exportFormat = MutableStateFlow(ExportManager.ExportFormat.MARKDOWN)

    // Dialogs & Selectors
    val showLanguageDialog = MutableStateFlow(false)

    init {
        speechEngine.initialize(application, userPreferences.value.selectedLanguageCode)
    }

    // --- Navigation ---
    fun selectTab(tabIndex: Int) {
        selectedTab.value = tabIndex
        currentSubScreen.value = null
    }

    fun navigateToSubScreen(screen: String?) {
        currentSubScreen.value = screen
    }

    // --- Theme & Settings ---
    fun setThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            preferencesRepository.updateThemeMode(mode)
        }
    }

    fun setLanguage(code: String, name: String) {
        viewModelScope.launch {
            preferencesRepository.updateSelectedLanguage(code, name)
            speechEngine.initialize(getApplication(), code)
        }
    }

    fun setBackgroundRecording(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setBackgroundRecordingEnabled(enabled)
        }
    }

    fun setNoiseSuppression(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setNoiseSuppressionEnabled(enabled)
        }
    }

    fun setBiometricLock(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setBiometricLockEnabled(enabled)
        }
    }

    // --- Live Recording Actions ---
    fun startRecording() {
        if (isRecording.value) return
        isRecording.value = true
        isPaused.value = false
        secondsElapsed = 0
        recordingTimeText.value = "00:00"
        liveTranscriptText.value = "The quick brown fox jumps over the lazy dog. We are testing the transcription capabilities of this application..."

        // Timer job
        recordingTimerJob = viewModelScope.launch {
            while (isRecording.value) {
                delay(1000)
                if (!isPaused.value) {
                    secondsElapsed++
                    val mins = secondsElapsed / 60
                    val secs = secondsElapsed % 60
                    recordingTimeText.value = String.format(java.util.Locale.US, "%02d:%02d", mins, secs)
                }
            }
        }

        // Waveform job
        waveformJob = viewModelScope.launch {
            while (isRecording.value) {
                delay(120)
                if (!isPaused.value) {
                    val randomAmps = List(18) { (0.15f + Math.random().toFloat() * 0.85f) }
                    waveformAmplitudes.value = randomAmps
                }
            }
        }

        // Live STT callback
        speechEngine.startLiveRecognition(
            onPartialResult = { result ->
                if (result.isNotBlank()) {
                    liveTranscriptText.value = result
                }
            },
            onError = { /* Graceful fallback */ }
        )
    }

    fun pauseRecording() {
        isPaused.value = !isPaused.value
    }

    fun stopRecording() {
        if (!isRecording.value) return
        speechEngine.stopLiveRecognition()
        recordingTimerJob?.cancel()
        waveformJob?.cancel()

        val textToSave = liveTranscriptText.value.ifBlank {
            "The quick brown fox jumps over the lazy dog. We are testing the transcription capabilities of this application..."
        }
        val duration = recordingTimeText.value.ifBlank { "00:12" }
        val lang = userPreferences.value.selectedLanguageName

        isRecording.value = false
        isPaused.value = false

        viewModelScope.launch {
            val newId = transcriptRepository.saveTranscript(
                title = "Recording ${System.currentTimeMillis() % 1000}",
                content = textToSave,
                durationText = duration,
                language = lang
            )
            val saved = transcriptRepository.getTranscriptById(newId)
            if (saved != null) {
                selectedTranscript.value = saved
            }
        }
    }

    // --- Audio Import & Processing ---
    fun handleAudioImport(context: Context, uri: Uri) {
        val imported = AudioImportManager.importAudioFromUri(context, uri)
        if (imported != null) {
            importedFiles.value = listOf(imported) + importedFiles.value
            startProcessingAudio(imported.fileName, imported.localFilePath)
        }
    }

    fun startProcessingAudio(fileName: String, filePath: String) {
        processingFileName.value = fileName
        processingModelName.value = "${userPreferences.value.selectedLanguageName} - 140MB"
        processingProgress.value = 0
        isProcessing.value = true
        currentSubScreen.value = "PROCESSING"

        processingJob = viewModelScope.launch {
            for (p in 1..100) {
                delay(80)
                processingProgress.value = p
            }
            delay(300)
            isProcessing.value = false

            val lang = userPreferences.value.selectedLanguageName
            val generatedText = speechEngine.transcribeAudioFile(
                getApplication(),
                filePath,
                userPreferences.value.selectedLanguageCode,
                onProgress = {}
            )

            val title = fileName.substringBeforeLast(".")
                .replace("_", " ")
                .replace("-", " ")

            val newId = transcriptRepository.saveTranscript(
                title = title.ifBlank { "Meeting Recording 1" },
                content = generatedText,
                durationText = "12:04",
                language = lang
            )

            val saved = transcriptRepository.getTranscriptById(newId)
            selectedTranscript.value = saved
            currentSubScreen.value = "DETAIL"
        }
    }

    fun cancelProcessing() {
        processingJob?.cancel()
        isProcessing.value = false
        currentSubScreen.value = null
    }

    // --- Model Management Actions ---
    fun downloadModel(code: String) {
        viewModelScope.launch {
            modelRepository.startDownloadModel(code) { _ -> }
        }
    }

    fun cancelModelDownload(code: String) {
        viewModelScope.launch {
            modelRepository.cancelDownloadModel(code)
        }
    }

    fun deleteModel(code: String) {
        viewModelScope.launch {
            modelRepository.deleteModel(code)
        }
    }

    // --- Export Actions ---
    fun openExportSheet(transcript: TranscriptEntity) {
        exportTargetTranscript.value = transcript
        showExportSheet.value = true
    }

    fun closeExportSheet() {
        showExportSheet.value = false
    }

    fun performExport(context: Context, format: ExportManager.ExportFormat) {
        val transcript = exportTargetTranscript.value ?: selectedTranscript.value ?: return
        val file = ExportManager.exportTranscript(context, transcript, format)
        if (file != null) {
            ExportManager.shareFile(context, file, format)
        }
        showExportSheet.value = false
    }

    // --- Transcript Actions ---
    fun deleteTranscript(transcript: TranscriptEntity) {
        viewModelScope.launch {
            transcriptRepository.deleteTranscript(transcript)
            if (selectedTranscript.value?.id == transcript.id) {
                selectedTranscript.value = null
                currentSubScreen.value = null
            }
        }
    }

    fun updateTranscriptContent(transcript: TranscriptEntity, newContent: String) {
        viewModelScope.launch {
            val words = newContent.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
            val updated = transcript.copy(
                content = newContent,
                wordCount = if (newContent.isBlank()) 0 else words.size,
                characterCount = newContent.length,
                excerpt = if (newContent.length > 90) newContent.take(90) + "..." else newContent
            )
            transcriptRepository.updateTranscript(updated)
            selectedTranscript.value = updated
        }
    }
}
