package com.example.odyway.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.odyway.R
import com.example.odyway.data.local.SettingsManager
import com.example.odyway.ui.theme.OdyWayTheme
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfileScreen(settingsManager: SettingsManager, onNavigateToSettings: () -> Unit) {
    val username by settingsManager.usernameFlow.collectAsState()
    val birthDate by settingsManager.birthDateFlow.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf(
        stringResource(id = R.string.profile_tab_recent),
        stringResource(id = R.string.profile_tab_favorites),
        stringResource(id = R.string.profile_tab_stats)
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = stringResource(id = R.string.profile_settings_desc),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(38.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(bottom = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Image(
                            painter = painterResource(id = R.drawable.icon_odyway),
                            contentDescription = stringResource(id = R.string.profile_pic_desc),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(124.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                        )
                        IconButton(
                            onClick = { /* TODO: Edit Photo */ },
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.error, CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = stringResource(id = R.string.profile_edit_desc),
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = username,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "@${username.lowercase().replace(" ", "")}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    if (birthDate > 0) {
                        val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
                        val birthDateDisplay = dateFormatter.format(Date(birthDate))
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Cake,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = birthDateDisplay,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStatItem(stringResource(id = R.string.profile_trips_label), "12")
                ProfileStatItem(stringResource(id = R.string.profile_countries_label), "5")
                ProfileStatItem(stringResource(id = R.string.profile_photos_label), "148")
            }

            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTabIndex == index) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                when (selectedTabIndex) {
                    0 -> Text(stringResource(id = R.string.profile_no_recent_trips), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                    1 -> Text(stringResource(id = R.string.profile_no_favorites), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                    2 -> Text(stringResource(id = R.string.profile_stats_coming_soon), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
            }
        }
    }
}

@Composable
fun ProfileStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}

@Preview(showBackground = true, name = "Profile Mode Light")
@Composable
fun ProfileScreenPreviewLight() {
    val context = LocalContext.current
    OdyWayTheme(darkTheme = false) {
        ProfileScreen(settingsManager = SettingsManager(context), onNavigateToSettings = {})
    }
}

@Preview(showBackground = true, name = "Profile Mode Dark")
@Composable
fun ProfileScreenPreviewDark() {
    val context = LocalContext.current
    OdyWayTheme(darkTheme = true) {
        ProfileScreen(settingsManager = SettingsManager(context), onNavigateToSettings = {})
    }
}
