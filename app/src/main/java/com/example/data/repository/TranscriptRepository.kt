package com.example.data.repository

import com.example.data.local.TranscriptDao
import com.example.data.local.TranscriptEntity
import kotlinx.coroutines.flow.Flow

class TranscriptRepository(private val transcriptDao: TranscriptDao) {

    val allTranscripts: Flow<List<TranscriptEntity>> = transcriptDao.getAllTranscripts()

    fun searchTranscripts(query: String): Flow<List<TranscriptEntity>> {
        return if (query.isBlank()) {
            transcriptDao.getAllTranscripts()
        } else {
            transcriptDao.searchTranscripts(query)
        }
    }

    suspend fun getTranscriptById(id: Long): TranscriptEntity? {
        return transcriptDao.getTranscriptById(id)
    }

    suspend fun saveTranscript(
        title: String,
        content: String,
        durationText: String,
        language: String,
        audioPath: String? = null
    ): Long {
        val words = content.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        val wordCount = if (content.isBlank()) 0 else words.size
        val charCount = content.length
        val excerpt = if (content.length > 90) content.take(90) + "..." else content

        val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.US)
        val dateFormatted = dateFormat.format(java.util.Date())

        val entity = TranscriptEntity(
            title = title,
            dateFormatted = dateFormatted,
            timestamp = System.currentTimeMillis(),
            durationText = durationText,
            content = content,
            wordCount = wordCount,
            characterCount = charCount,
            language = language,
            audioPath = audioPath,
            excerpt = excerpt
        )
        return transcriptDao.insertTranscript(entity)
    }

    suspend fun updateTranscript(transcript: TranscriptEntity) {
        transcriptDao.updateTranscript(transcript)
    }

    suspend fun deleteTranscript(transcript: TranscriptEntity) {
        transcriptDao.deleteTranscript(transcript)
    }

    suspend fun deleteTranscriptById(id: Long) {
        transcriptDao.deleteTranscriptById(id)
    }

    suspend fun clearAll() {
        transcriptDao.deleteAllTranscripts()
    }
}
