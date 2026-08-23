package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PrimaryBlue

data class LanguageItem(val code: String, val name: String)

@Composable
fun LanguageSelectorDialog(
    currentCode: String,
    onDismiss: () -> Unit,
    onLanguageSelected: (code: String, name: String) -> Unit
) {
    val languages = listOf(
        LanguageItem("en-UK", "English (UK)"),
        LanguageItem("bn", "Bangla"),
        LanguageItem("hi", "Hindi"),
        LanguageItem("es", "Spanish"),
        LanguageItem("fr", "French"),
        LanguageItem("de", "German")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Select Language",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = PrimaryBlue
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                languages.forEach { lang ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onLanguageSelected(lang.code, lang.name)
                                onDismiss()
                            }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = lang.name,
                            fontSize = 16.sp,
                            fontWeight = if (lang.code == currentCode) FontWeight.Bold else FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (lang.code == currentCode) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = PrimaryBlue
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = PrimaryBlue, fontWeight = FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}
