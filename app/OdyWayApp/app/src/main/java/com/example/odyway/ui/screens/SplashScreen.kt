package com.example.odyway.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.odyway.R
import kotlinx.coroutines.delay
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.odyway.ui.theme.OdyWayTheme

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(2000)
        onNavigateToLogin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Logo
            Image(
                painter = painterResource(id = R.drawable.icon_odyway),
                contentDescription = stringResource(id = R.string.splash_logo_desc),
                modifier = Modifier
                    .size(180.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Loading
            LinearProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(id = R.string.splash_loading),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Text(
            text = stringResource(id = R.string.splash_version),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 34.dp)
        )
    }
}

@Preview(showBackground = true, name = "Splash Mode Light")
@Composable
fun SplashScreenPreviewLight() {
    OdyWayTheme(darkTheme = false) {
        SplashScreen(onNavigateToLogin = {})
    }
}

@Preview(showBackground = true, name = "Splash Mode Dark")
@Composable
fun SplashScreenPreviewDark() {
    OdyWayTheme(darkTheme = true) {
        SplashScreen(onNavigateToLogin = {})
    }
}