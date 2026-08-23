package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TranscriptDao {

    @Query("SELECT * FROM transcripts ORDER BY timestamp DESC")
    fun getAllTranscripts(): Flow<List<TranscriptEntity>>

    @Query("SELECT * FROM transcripts WHERE id = :id")
    suspend fun getTranscriptById(id: Long): TranscriptEntity?

    @Query("SELECT * FROM transcripts WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchTranscripts(query: String): Flow<List<TranscriptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTranscript(transcript: TranscriptEntity): Long

    @Update
    suspend fun updateTranscript(transcript: TranscriptEntity)

    @Delete
    suspend fun deleteTranscript(transcript: TranscriptEntity)

    @Query("DELETE FROM transcripts WHERE id = :id")
    suspend fun deleteTranscriptById(id: Long)

    @Query("DELETE FROM transcripts")
    suspend fun deleteAllTranscripts()
}
