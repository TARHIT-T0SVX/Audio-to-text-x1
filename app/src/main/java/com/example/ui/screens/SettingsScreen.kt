package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.preferences.AppThemeMode
import com.example.ui.theme.PrimaryBlue
import com.example.ui.viewmodel.MainViewModel

@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val userPrefs by viewModel.userPreferences.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Section 1: Appearance & Theme
        SettingsSectionTitle("APPEARANCE")
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Theme",
                        tint = PrimaryBlue
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "App Theme Mode",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                ThemeRadioButton(
                    label = "Light Theme",
                    isSelected = userPrefs.themeMode == AppThemeMode.LIGHT,
                    onClick = { viewModel.setThemeMode(AppThemeMode.LIGHT) }
                )

                ThemeRadioButton(
                    label = "Dark Theme",
                    isSelected = userPrefs.themeMode == AppThemeMode.DARK,
                    onClick = { viewModel.setThemeMode(AppThemeMode.DARK) }
                )

                ThemeRadioButton(
                    label = "System Default",
                    isSelected = userPrefs.themeMode == AppThemeMode.SYSTEM,
                    onClick = { viewModel.setThemeMode(AppThemeMode.SYSTEM) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section 2: Recording & Background
        SettingsSectionTitle("RECORDING & PROCESSING")
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SettingsSwitchRow(
                    title = "Background Recording",
                    subtitle = "Allows recording service to continue when minimized or screen off",
                    icon = Icons.Default.Mic,
                    isChecked = userPrefs.isBackgroundRecordingEnabled,
                    onCheckedChange = { viewModel.setBackgroundRecording(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SettingsSwitchRow(
                    title = "Noise Suppression",
                    subtitle = "Filters out ambient noise during dictation",
                    icon = Icons.Default.DarkMode,
                    isChecked = userPrefs.isNoiseSuppressionEnabled,
                    onCheckedChange = { viewModel.setNoiseSuppression(it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section 3: Privacy & Language
        SettingsSectionTitle("PRIVACY & LANGUAGE")
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.showLanguageDialog.value = true }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = PrimaryBlue
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Primary Language",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = userPrefs.selectedLanguageName,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Select",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                SettingsSwitchRow(
                    title = "Biometric Lock",
                    subtitle = "Protect sensitive transcript history with fingerprint/face",
                    icon = Icons.Default.Fingerprint,
                    isChecked = userPrefs.isBiometricLockEnabled,
                    onCheckedChange = { viewModel.setBiometricLock(it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section 4: Data & Storage
        SettingsSectionTitle("STORAGE & CACHE")
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            tint = PrimaryBlue
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Clear Cache",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Reclaim local audio temp space",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Text(
                        text = "12 MB",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = PrimaryBlue
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
        letterSpacing = 1.sp
    )
}

@Composable
fun ThemeRadioButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface
        )

        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
        )
    }
}

@Composable
fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryBlue
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryBlue)
        )
    }
}
