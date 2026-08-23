package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.export.ExportManager
import com.example.ui.components.ExportBottomSheet
import com.example.ui.components.LanguageSelectorDialog
import com.example.ui.components.TranscribeBottomBar
import com.example.ui.components.TranscribeTopBar
import com.example.ui.screens.AudioImportScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ModelsScreen
import com.example.ui.screens.ProcessingScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.TranscribeTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestPermissionsIfNeeded()

        setContent {
            val userPrefs by viewModel.userPreferences.collectAsState()
            val selectedTab by viewModel.selectedTab.collectAsState()
            val currentSubScreen by viewModel.currentSubScreen.collectAsState()
            val showExportSheet by viewModel.showExportSheet.collectAsState()
            val showLanguageDialog by viewModel.showLanguageDialog.collectAsState()

            TranscribeTheme(themeMode = userPrefs.themeMode) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        if (currentSubScreen == null) {
                            TranscribeTopBar(
                                title = "Transcribe",
                                onMenuClick = { viewModel.selectTab(3) },
                                onLanguageClick = { viewModel.showLanguageDialog.value = true }
                            )
                        }
                    },
                    bottomBar = {
                        if (currentSubScreen == null) {
                            TranscribeBottomBar(
                                selectedTab = selectedTab,
                                onTabSelected = { index -> viewModel.selectTab(index) }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentSubScreen) {
                            "IMPORT" -> {
                                AudioImportScreen(
                                    viewModel = viewModel,
                                    onBackClick = { viewModel.navigateToSubScreen(null) }
                                )
                            }
                            "PROCESSING" -> {
                                ProcessingScreen(
                                    viewModel = viewModel,
                                    onClose = { viewModel.cancelProcessing() }
                                )
                            }
                            else -> {
                                when (selectedTab) {
                                    0 -> HomeScreen(
                                        viewModel = viewModel,
                                        onNavigateToImport = { viewModel.navigateToSubScreen("IMPORT") }
                                    )
                                    1 -> HistoryScreen(
                                        viewModel = viewModel,
                                        onSelectTranscript = { transcript ->
                                            viewModel.selectedTranscript.value = transcript
                                            viewModel.selectTab(0)
                                        }
                                    )
                                    2 -> ModelsScreen(viewModel = viewModel)
                                    3 -> SettingsScreen(viewModel = viewModel)
                                }
                            }
                        }

                        if (showExportSheet) {
                            ExportBottomSheet(
                                onDismiss = { viewModel.closeExportSheet() },
                                onExportSelected = { format ->
                                    viewModel.performExport(this@MainActivity, format)
                                }
                            )
                        }

                        if (showLanguageDialog) {
                            LanguageSelectorDialog(
                                currentCode = userPrefs.selectedLanguageCode,
                                onDismiss = { viewModel.showLanguageDialog.value = false },
                                onLanguageSelected = { code, name ->
                                    viewModel.setLanguage(code, name)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun requestPermissionsIfNeeded() {
        val permissions = mutableListOf(Manifest.permission.RECORD_AUDIO)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        requestPermissionLauncher.launch(permissions.toTypedArray())
    }
}
