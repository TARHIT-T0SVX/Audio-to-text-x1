package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ModelTraining
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ModelTraining
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PrimaryBlue

data class NavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun TranscribeBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val items = listOf(
        NavItem("Home", Icons.Filled.Home, Icons.Outlined.Home),
        NavItem("History", Icons.Filled.History, Icons.Outlined.History),
        NavItem("Models", Icons.Filled.ModelTraining, Icons.Outlined.ModelTraining),
        NavItem("Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = selectedTab == index
                val pillColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f) else Color.Transparent,
                    label = "pill"
                )
                val iconColor by animateColorAsState(
                    targetValue = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    label = "icon"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clickable { onTabSelected(index) }
                        .padding(horizontal = 12.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .width(64.dp)
                            .height(32.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(pillColor)
                    ) {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.label,
                            tint = iconColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = item.label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = iconColor,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
