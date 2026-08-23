package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [TranscriptEntity::class, LanguageModelEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transcriptDao(): TranscriptDao
    abstract fun languageModelDao(): LanguageModelDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "transcribe_database"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate default models and initial transcripts matching the reference UI!
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                populateInitialData(database)
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateInitialData(database: AppDatabase) {
            val defaultModels = listOf(
                LanguageModelEntity(code = "en-UK", name = "English (UK)", sizeMB = 140, isInstalled = true),
                LanguageModelEntity(code = "bn", name = "Bangla", sizeMB = 210, isInstalled = false),
                LanguageModelEntity(code = "hi", name = "Hindi", sizeMB = 180, isInstalled = false, isDownloading = true, downloadProgress = 45),
                LanguageModelEntity(code = "es", name = "Spanish", sizeMB = 160, isInstalled = false),
                LanguageModelEntity(code = "fr", name = "French", sizeMB = 155, isInstalled = false),
                LanguageModelEntity(code = "de", name = "German", sizeMB = 175, isInstalled = false)
            )
            database.languageModelDao().insertModels(defaultModels)

            val initialTranscripts = listOf(
                TranscriptEntity(
                    title = "Client Meeting",
                    dateFormatted = "May 12, 2024",
                    timestamp = System.currentTimeMillis() - 86400000 * 10,
                    durationText = "12:04",
                    content = "Discussion regarding the Q3 product roadmap, focusing primarily on the integration of new offline AI models, performance optimizations, and multi-language support. We reviewed team bandwidth and aligned on core release milestones.",
                    wordCount = 420,
                    characterCount = 2850,
                    language = "English (UK)",
                    excerpt = "Discussion regarding the Q3 product roadmap, focusing primarily on the integration of new..."
                ),
                TranscriptEntity(
                    title = "Interview: Dr. Smith",
                    dateFormatted = "May 10, 2024",
                    timestamp = System.currentTimeMillis() - 86400000 * 12,
                    durationText = "45:12",
                    content = "An in-depth look at the latest advancements in natural language processing and how edge computing enables instant speech recognition without internet access. Dr. Smith highlighted the critical privacy benefits of keeping sensitive audio data strictly local.",
                    wordCount = 3105,
                    characterCount = 18400,
                    language = "English (UK)",
                    excerpt = "An in-depth look at the latest advancements in natural language processing and how edge..."
                ),
                TranscriptEntity(
                    title = "Weekly Sync",
                    dateFormatted = "May 08, 2024",
                    timestamp = System.currentTimeMillis() - 86400000 * 14,
                    durationText = "28:30",
                    content = "Standard weekly team sync covering blocker updates, milestone progress, and planning for upcoming architecture refactoring. Action items assigned for background service testing.",
                    wordCount = 1890,
                    characterCount = 11200,
                    language = "English (UK)",
                    excerpt = "Standard weekly team sync covering blocker updates, milestone progress, and planning for..."
                ),
                TranscriptEntity(
                    title = "Lecture Notes",
                    dateFormatted = "May 05, 2024",
                    timestamp = System.currentTimeMillis() - 86400000 * 17,
                    durationText = "55:00",
                    content = "Introduction to advanced data structures, specifically focusing on self-balancing binary search trees, B-trees, and memory layout optimization for mobile devices.",
                    wordCount = 4210,
                    characterCount = 24900,
                    language = "English (UK)",
                    excerpt = "Introduction to advanced data structures, specifically focusing on self-balancing binary..."
                )
            )
            for (t in initialTranscripts) {
                database.transcriptDao().insertTranscript(t)
            }
        }
    }
}
