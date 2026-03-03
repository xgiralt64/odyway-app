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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.odyway.R

import com.example.odyway.ui.theme.NavyBlue
import com.example.odyway.ui.theme.CyanBlue
import com.example.odyway.ui.theme.BackgroundLight
import com.example.odyway.ui.theme.White
import com.example.odyway.ui.theme.MapPinRed

@Composable
fun ProfileScreen() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Recent", "Favorites", "Stats")

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            // Top Bar con icono de Ajustes (Settings)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavyBlue)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { /* TODO: Navigate to Preferences/Settings */ }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = White,
                        modifier = Modifier.size(38.dp))
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
            // --- seccion superior foto de perfil y nombres ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavyBlue)
                    .padding(bottom = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Foto de perfil con botón de editar
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Image(
                            painter = painterResource(id = R.drawable.icon_odyway), // Placeholder del usuario
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(124.dp)
                                .clip(CircleShape)
                                .border(1.dp, White)
                                .background(White)
                        )
                        // para editar
                        IconButton(
                            onClick = { /* TODO: Edit Photo */ },
                            modifier = Modifier
                                .size(36.dp)
                                .background(MapPinRed, CircleShape)
                                .border(2.dp, White, CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // mnombre y Usuario
                    Text(
                        text = "Xavi Xabon", // Ejemplo en español
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    Text(
                        text = "@mamwebo_travels",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CyanBlue
                    )
                }
            }

            // --- informacion de usuario ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStatItem("Trips", "12")
                ProfileStatItem("Countries", "5")
                ProfileStatItem("Photos", "148")
            }

            // --- tabs de perfil ---
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = BackgroundLight,
                contentColor = NavyBlue,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = MapPinRed
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
                                color = if (selectedTabIndex == index) MapPinRed else NavyBlue,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // --- contenido de tabs mock por ahora ---
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                when (selectedTabIndex) {
                    0 -> Text("No recent trips found", color = Color.Gray)
                    1 -> Text("You haven't added favorites yet", color = Color.Gray)
                    2 -> Text("Travel statistics coming soon", color = Color.Gray)
                }
            }
        }
    }
}

// Componente para los numeritos de estadística del perfil
@Composable
fun ProfileStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = NavyBlue
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Preview
@Composable
fun Preview() {
    ProfileScreen()
}