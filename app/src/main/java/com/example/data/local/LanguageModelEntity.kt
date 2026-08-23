package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "language_models")
data class LanguageModelEntity(
    @PrimaryKey val code: String, // e.g. "en-UK", "bn", "hi"
    val name: String,             // e.g. "English (UK)", "Bangla", "Hindi"
    val sizeMB: Int,              // e.g. 140, 210, 180
    val isInstalled: Boolean,
    val isDownloading: Boolean = false,
    val downloadProgress: Int = 0, // 0..100
    val version: String = "1.0.2",
    val lastUsedTimestamp: Long = 0
)
