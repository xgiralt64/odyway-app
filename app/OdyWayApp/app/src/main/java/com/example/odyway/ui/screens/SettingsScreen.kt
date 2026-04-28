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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.odyway.R
import com.example.odyway.ui.theme.OdyWayTheme

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPreferences: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToTerms: () -> Unit,
    onNavigateToAccountInfo: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back_button),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.settings_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
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

            // --- GRUPO 1: ACCOUNT & PREFERENCES ---
            item {
                SettingsGroup(title = stringResource(id = R.string.settings_group_account)) {
                    SettingsItem(
                        icon = Icons.Filled.Person,
                        title = stringResource(id = R.string.settings_account_info),
                        subtitle = stringResource(id = R.string.settings_account_info_sub),
                        onClick = onNavigateToAccountInfo
                    )
                    ManualDivider()
                    SettingsItem(
                        icon = Icons.Filled.Settings,
                        title = stringResource(id = R.string.settings_preferences),
                        subtitle = stringResource(id = R.string.settings_preferences_sub),
                        onClick = onNavigateToPreferences
                    )
                }
            }

            // --- GRUPO 2: SUPPORT & INFO ---
            item {
                SettingsGroup(title = stringResource(id = R.string.settings_group_support)) {
                    SettingsItem(
                        icon = Icons.Filled.Info,
                        title = stringResource(id = R.string.settings_help),
                        subtitle = stringResource(id = R.string.settings_help_sub),
                        onClick = { /* TODO */ }
                    )
                    ManualDivider()
                    SettingsItem(
                        icon = Icons.Filled.Star,
                        title = stringResource(id = R.string.settings_about),
                        subtitle = stringResource(id = R.string.settings_about_sub),
                        onClick = onNavigateToAbout
                    )
                    ManualDivider()
                    SettingsItem(
                        icon = Icons.Filled.List,
                        title = stringResource(id = R.string.settings_terms),
                        subtitle = stringResource(id = R.string.settings_terms_sub),
                        onClick = onNavigateToTerms
                    )
                }
            }

            // --- GRUPO 3: ACTIONS ---
            item {
                SettingsGroup(title = stringResource(id = R.string.settings_group_actions)) {
                    SettingsItem(
                        icon = Icons.Filled.Warning,
                        title = stringResource(id = R.string.settings_logout),
                        subtitle = stringResource(id = R.string.settings_logout_sub),
                        iconTint = MaterialTheme.colorScheme.error, // El rojo se ve bien en ambos fondos
                        titleColor = MaterialTheme.colorScheme.error,
                        onClick = onLogoutClick
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

// =====================================
// COMPONENTES REUTILIZABLES
// =====================================

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
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
    // ¡AQUÍ ESTÁ LA MAGIA! Cambiado de primary a onSurface
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = stringResource(id = R.string.settings_go_desc),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun ManualDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
    )
}

//@Preview(showBackground = true, name = "Settings Mode Light")
//@Composable
//fun SettingsScreenPreviewLight() {
//    OdyWayTheme(darkTheme = false) {
//        SettingsScreen(
//            onNavigateBack = {},
//            onNavigateToPreferences = {},
//            onNavigateToAbout = {},
//            onNavigateToTerms = {},
//            onNavigateToAccountInfo = {}
//        )
//    }
//}
//
//@Preview(showBackground = true, name = "Settings Mode Dark")
//@Composable
//fun SettingsScreenPreviewDark() {
//    OdyWayTheme(darkTheme = true) {
//        SettingsScreen(
//            onNavigateBack = {},
//            onNavigateToPreferences = {},
//            onNavigateToAbout = {},
//            onNavigateToTerms = {},
//            onNavigateToAccountInfo = {}
//        )
//    }
//}