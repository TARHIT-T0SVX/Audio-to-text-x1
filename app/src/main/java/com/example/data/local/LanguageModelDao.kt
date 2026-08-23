package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LanguageModelDao {

    @Query("SELECT * FROM language_models ORDER BY name ASC")
    fun getAllModels(): Flow<List<LanguageModelEntity>>

    @Query("SELECT * FROM language_models WHERE code = :code")
    suspend fun getModelByCode(code: String): LanguageModelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModels(models: List<LanguageModelEntity>)

    @Update
    suspend fun updateModel(model: LanguageModelEntity)

    @Query("UPDATE language_models SET isInstalled = :isInstalled, isDownloading = :isDownloading, downloadProgress = :progress WHERE code = :code")
    suspend fun updateDownloadStatus(code: String, isInstalled: Boolean, isDownloading: Boolean, progress: Int)
}
