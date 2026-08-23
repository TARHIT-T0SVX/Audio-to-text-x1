package com.example.audio

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream

data class ImportedAudioFile(
    val uri: Uri,
    val fileName: String,
    val sizeMB: String,
    val durationText: String,
    val localFilePath: String,
    val timestampText: String = "Today",
    val isTranscribed: Boolean = false
)

object AudioImportManager {

    fun importAudioFromUri(context: Context, uri: Uri): ImportedAudioFile? {
        val contentResolver = context.contentResolver
        var fileName = "imported_audio_${System.currentTimeMillis()}.mp3"
        var fileSizeByte = 0L

        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (cursor.moveToFirst()) {
                if (nameIndex != -1) fileName = cursor.getString(nameIndex) ?: fileName
                if (sizeIndex != -1) fileSizeByte = cursor.getLong(sizeIndex)
            }
        }

        val importDir = File(context.cacheDir, "imported_audio").apply { mkdirs() }
        val tempFile = File(importDir, "${System.currentTimeMillis()}_$fileName")

        try {
            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        var durationText = "05:20"
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(tempFile.absolutePath)
            val durationMsStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val durationMs = durationMsStr?.toLongOrNull() ?: 0L
            val seconds = (durationMs / 1000) % 60
            val minutes = (durationMs / (1000 * 60)) % 60
            val hours = durationMs / (1000 * 60 * 60)
            durationText = if (hours > 0) {
                String.format(java.util.Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds)
            }
            retriever.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val sizeMB = if (fileSizeByte > 0) {
            String.format(java.util.Locale.US, "%.1f MB", fileSizeByte / (1024.0 * 1024.0))
        } else {
            "12 MB"
        }

        return ImportedAudioFile(
            uri = uri,
            fileName = fileName,
            sizeMB = sizeMB,
            durationText = durationText,
            localFilePath = tempFile.absolutePath,
            timestampText = "Today"
        )
    }
}
