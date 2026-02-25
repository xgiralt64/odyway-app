package com.example.odyway

import androidx.compose.runtime.*
import androidx.navigation.compose.*
import androidx.navigation.compose.NavHost
import com.example.odyway.ui.screens.*

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"   // Comencem per la splash
    ) {

        composable("splash") {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen()
        }


    }
}