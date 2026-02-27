package com.example.odyway.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.odyway.R
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.Color

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(2000)
        onNavigateToHome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Logo
            Image(                                      //CANVIAR PER EL LOGO REAL
                painter = painterResource(id = R.drawable.splash),
                contentDescription = "Logo OdyWay",
                modifier = Modifier
                    .size(180.dp)
            )


            Spacer(modifier = Modifier.height(32.dp))

            // Loading
            CircularProgressIndicator(
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Carregant...",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}