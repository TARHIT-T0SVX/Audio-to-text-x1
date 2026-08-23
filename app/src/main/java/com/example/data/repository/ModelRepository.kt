package com.example.data.repository

import com.example.data.local.LanguageModelDao
import com.example.data.local.LanguageModelEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

class ModelRepository(private val languageModelDao: LanguageModelDao) {

    val allModels: Flow<List<LanguageModelEntity>> = languageModelDao.getAllModels()

    suspend fun startDownloadModel(code: String, onProgress: (Int) -> Unit) {
        val model = languageModelDao.getModelByCode(code) ?: return
        languageModelDao.updateDownloadStatus(code, isInstalled = false, isDownloading = true, progress = 0)

        for (p in 1..100 step 5) {
            delay(120)
            onProgress(p)
            languageModelDao.updateDownloadStatus(code, isInstalled = false, isDownloading = true, progress = p)
        }

        languageModelDao.updateDownloadStatus(code, isInstalled = true, isDownloading = false, progress = 100)
    }

    suspend fun cancelDownloadModel(code: String) {
        languageModelDao.updateDownloadStatus(code, isInstalled = false, isDownloading = false, progress = 0)
    }

    suspend fun deleteModel(code: String) {
        languageModelDao.updateDownloadStatus(code, isInstalled = false, isDownloading = false, progress = 0)
    }
}
