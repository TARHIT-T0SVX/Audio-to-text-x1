package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.export.ExportManager
import com.example.ui.theme.AccentRed
import com.example.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportBottomSheet(
    onDismiss: () -> Unit,
    onExportSelected: (ExportManager.ExportFormat) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedFormat by remember { mutableStateOf(ExportManager.ExportFormat.MARKDOWN) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Export Transcript",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Option 1: Markdown (.md)
            ExportOptionCard(
                title = "Markdown (.md)",
                subtitle = "Best for web and technical documentation. Preserves basic formatting.",
                icon = Icons.Outlined.Code,
                iconColor = PrimaryBlue,
                isSelected = selectedFormat == ExportManager.ExportFormat.MARKDOWN,
                onClick = { selectedFormat = ExportManager.ExportFormat.MARKDOWN }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Option 2: Plain Text (.txt)
            ExportOptionCard(
                title = "Plain Text (.txt)",
                subtitle = "Simple text file without formatting. Universally compatible.",
                icon = Icons.Outlined.Description,
                iconColor = PrimaryBlue,
                isSelected = selectedFormat == ExportManager.ExportFormat.PLAIN_TEXT,
                onClick = { selectedFormat = ExportManager.ExportFormat.PLAIN_TEXT }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Option 3: PDF Document (.pdf)
            ExportOptionCard(
                title = "PDF Document (.pdf)",
                subtitle = "Locked layout ideal for sharing, printing, or formal archiving.",
                icon = Icons.Outlined.PictureAsPdf,
                iconColor = AccentRed,
                isSelected = selectedFormat == ExportManager.ExportFormat.PDF,
                onClick = { selectedFormat = ExportManager.ExportFormat.PDF }
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Bottom Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Cancel",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryBlue
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = { onExportSelected(selectedFormat) },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    modifier = Modifier
                        .height(48.dp)
                        .width(110.dp)
                ) {
                    Text(
                        text = "Export",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ExportOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.outline
    val strokeWidth = if (isSelected) 2.dp else 1.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(strokeWidth, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconColor.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = PrimaryBlue
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        lineHeight = 18.sp
                    )
                )
            }

            if (isSelected) {
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = PrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
