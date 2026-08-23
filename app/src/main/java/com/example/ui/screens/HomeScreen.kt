package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentRed
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.RoyalBlue
import com.example.ui.theme.SoftBluePill
import com.example.ui.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToImport: () -> Unit
) {
    val context = LocalContext.current
    val isRecording by viewModel.isRecording.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()
    val recordingTimeText by viewModel.recordingTimeText.collectAsState()
    val liveTranscriptText by viewModel.liveTranscriptText.collectAsState()
    val amplitudes by viewModel.waveformAmplitudes.collectAsState()
    val userPrefs by viewModel.userPreferences.collectAsState()
    val selectedTranscript by viewModel.selectedTranscript.collectAsState()

    var showCopyNotice by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 90.dp)
        ) {
            when {
                // State 1: Active Live Recording Screen (Screen 2 from reference image)
                isRecording -> {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = liveTranscriptText,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Normal,
                                fontSize = 26.sp,
                                lineHeight = 38.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    }

                    // Recording Controls Card Widget (Bottom of screen 2)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(AccentRed)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "RECORDING",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = AccentRed,
                                        letterSpacing = 0.5.sp
                                    )
                                }

                                Text(
                                    text = recordingTimeText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 28.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = userPrefs.selectedLanguageName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Animated sound wave bars & Pause/Stop controls
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Waveform
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    amplitudes.forEach { amp ->
                                        val animatedHeight by animateFloatAsState(targetValue = amp * 40f, label = "wave")
                                        Box(
                                            modifier = Modifier
                                                .width(4.dp)
                                                .height(animatedHeight.dp.coerceAtLeast(4.dp))
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(RoyalBlue)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { viewModel.pauseRecording() },
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        Icon(
                                            imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                            contentDescription = if (isPaused) "Resume" else "Pause",
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(PrimaryBlue)
                                            .clickable { viewModel.stopRecording() }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Stop,
                                            contentDescription = "Stop",
                                            tint = Color.White,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // State 2: Active Transcript Viewing (Screen 3 from reference image)
                selectedTranscript != null -> {
                    val transcript = selectedTranscript!!
                    var editableContent by remember(transcript.id) { mutableStateOf(transcript.content) }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = transcript.title,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "📅 ${transcript.dateFormatted} • ${transcript.durationText}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {
                                OutlinedTextField(
                                    value = editableContent,
                                    onValueChange = {
                                        editableContent = it
                                        viewModel.updateTranscriptContent(transcript, it)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent
                                    ),
                                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = 16.sp,
                                        lineHeight = 26.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Bottom action icons inside transcript card
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                        val clip = android.content.ClipData.newPlainText("Transcript", editableContent)
                                        clipboard.setPrimaryClip(clip)
                                        showCopyNotice = true
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy",
                                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }

                                    IconButton(onClick = {
                                        val sendIntent = android.content.Intent().apply {
                                            action = android.content.Intent.ACTION_SEND
                                            putExtra(android.content.Intent.EXTRA_TEXT, editableContent)
                                            type = "text/plain"
                                        }
                                        context.startActivity(android.content.Intent.createChooser(sendIntent, "Share Transcript"))
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Share",
                                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }

                                    IconButton(onClick = {
                                        viewModel.updateTranscriptContent(transcript, editableContent)
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Save,
                                            contentDescription = "Save",
                                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }

                                    IconButton(onClick = {
                                        viewModel.openExportSheet(transcript)
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Download,
                                            contentDescription = "Export",
                                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${transcript.wordCount} words | ${transcript.characterCount} characters",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Saved",
                                            tint = PrimaryBlue,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Saved",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryBlue
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // State 3: Idle Screen (Screen 1 from reference image)
                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(460.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(110.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "Microphone",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(28.dp))

                                Text(
                                    text = "Ready to Transcribe",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Your transcript will appear here.\nPress the microphone to start recording.",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 15.sp,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                        lineHeight = 22.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Action FABs (Import File & Record Mic)
        if (!isRecording) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.End
            ) {
                // Audio File Import button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onNavigateToImport() }
                ) {
                    Icon(
                        imageVector = Icons.Default.UploadFile,
                        contentDescription = "Audio Import",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Primary Record Floating Action Button
                FloatingActionButton(
                    onClick = { viewModel.startRecording() },
                    shape = CircleShape,
                    containerColor = PrimaryBlue,
                    contentColor = Color.White,
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Record",
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}
