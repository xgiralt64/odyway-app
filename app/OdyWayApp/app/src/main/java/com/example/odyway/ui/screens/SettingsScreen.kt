package com.example.odyway.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.odyway.ui.theme.NavyBlue
import com.example.odyway.ui.theme.BackgroundLight
import com.example.odyway.ui.theme.White
import com.example.odyway.ui.theme.MapPinRed

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPreferences: () -> Unit,
) {
    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavyBlue)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // account preferences
            item {
                SettingsGroup(title = "Account & Preferences") {
                    SettingsItem(
                        icon = Icons.Filled.Person,
                        title = "Account Information",
                        subtitle = "Manage your personal data",
                        onClick = { /* TODO */ }
                    )
                    ManualDivider()
                    SettingsItem(
                        icon = Icons.Filled.Settings,
                        title = "Preferences",
                        subtitle = "Language, Theme, Notifications (T4.4)",
                        onClick = onNavigateToPreferences
                    )
                }
            }

            // --- GRUPO 2: SUPPORT & INFO ---
            item {
                SettingsGroup(title = "Support & Information") {
                    SettingsItem(
                        icon = Icons.Filled.Info,
                        title = "Help & Support",
                        subtitle = "FAQ and contact",
                        onClick = { /* TODO */ }
                    )
                    ManualDivider()
                    SettingsItem(
                        icon = Icons.Filled.Star,
                        title = "About OdyWay",
                        subtitle = "Version, Team info (T4.2)",
                        onClick = { /* TODO */ }
                    )
                    ManualDivider()
                    SettingsItem(
                        icon = Icons.Filled.List,
                        title = "Terms & Conditions",
                        subtitle = "Legal agreements (T4.3)",
                        onClick = { /* TODO */ }
                    )
                }
            }

            // actions
            item {
                SettingsGroup(title = "Actions") {
                    SettingsItem(
                        icon = Icons.Filled.Warning,
                        title = "Log Out",
                        subtitle = "Sign out of your account",
                        iconTint = MapPinRed,
                        titleColor = MapPinRed,
                        onClick = { /* TODO */ }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

// reutilizables

@Composable
fun SettingsGroup(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(content = content)
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    iconTint: Color = NavyBlue,
    titleColor: Color = NavyBlue,
    onClick: () -> Unit
) {
    // Surface genera un efecto de onda Ripple rectangular perfecto y nativo
    Surface(
        onClick = onClick,
        color = Color.Transparent, // Transparente para respetar el color de la tarjeta
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // El padding va dentro del Surface para que la onda llegue a los bordes
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = titleColor
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = "Go",
                tint = Color.Gray
            )
        }
    }
}

// Separador
@Composable
fun ManualDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.LightGray.copy(alpha = 0.3f))
    )
}