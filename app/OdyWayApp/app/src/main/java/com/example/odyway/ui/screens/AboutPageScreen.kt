package com.example.odyway.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.odyway.R
import com.example.odyway.ui.theme.BackgroundLight
import com.example.odyway.ui.theme.CyanBlue
import com.example.odyway.ui.theme.NavyBlue
import com.example.odyway.ui.theme.White

@Composable
fun AboutScreen(onNavigateBack: () -> Unit) {
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
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "About OdyWay",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // LOGO Y NOMBRE
            Image(
                painter = painterResource(id = R.drawable.icon_odyway),
                contentDescription = "OdyWay Logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Ody Way",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = NavyBlue
            )

            // VERSIÓN -
            Text(
                text = "Version 0.1.0 (Sprint 01)",
                style = MaterialTheme.typography.bodyMedium,
                color = CyanBlue,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Contenedor de información tipo tarjeta
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AboutInfoSection(title = "Development Team", info = "Jonas Obando\nXavier Giralt")

                    Divider(color = Color.LightGray.copy(alpha = 0.5f))

                    AboutInfoSection(title = "Technical Information", info = "Built con Android Jetpack Compose\nKotlin & Material Design 3\nAPI 35")

                    Divider(color = Color.LightGray.copy(alpha = 0.5f))

                    AboutInfoSection(title = "License", info = "OdyWay © 2025\nAll rights reserved.\nUdL - Sprint 01")
                }
            }
        }
    }
}

@Composable
fun AboutInfoSection(title: String, info: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            color = NavyBlue,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = info,
            color = Color.DarkGray,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}
