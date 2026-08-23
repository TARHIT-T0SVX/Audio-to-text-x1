package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transcripts")
data class TranscriptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val dateFormatted: String,
    val timestamp: Long = System.currentTimeMillis(),
    val durationText: String,
    val content: String,
    val wordCount: Int,
    val characterCount: Int,
    val language: String,
    val audioPath: String? = null,
    val isFavorite: Boolean = false,
    val excerpt: String = ""
)
