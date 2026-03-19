package com.example.odyway

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.*
import androidx.navigation.compose.NavHost
import com.example.odyway.data.local.SettingsManager
import com.example.odyway.ui.screens.*
import com.example.odyway.ui.theme.MapPinRed
import com.example.odyway.ui.theme.NavyBlue

// sealed class amb resource IDs per a la traducció
sealed class Screen(
    val route: String,
    val titleRes: Int? = null,
    val icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    data object Home : Screen("home", R.string.nav_home, Icons.Filled.Home)
    data object Login : Screen("login", R.string.login_button, Icons.Filled.Login)
    data object Trips : Screen("trips", R.string.nav_trips, Icons.Filled.Place)
    data object Profile : Screen("profile", R.string.nav_profile, Icons.Filled.Person)
    data object Splash : Screen("splash")
    data object Settings : Screen("settings")
    data object Preferences : Screen("preferences")
    data object About : Screen("about")
    data object TermsConditions : Screen("termsconditions")
    data object AccountInfo : Screen("accountinfo")
}

@Composable
fun NavGraph(settingsManager: SettingsManager) {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Usamos las constantes de tu sealed class para evitar errores de tipeo
    val routesWithBottomBar = listOf(Screen.Home.route, Screen.Trips.route, Screen.Profile.route)

    Scaffold(
        bottomBar = {
            if (currentRoute in routesWithBottomBar) {
                BottomNavigationBar(
                    navController = navController,
                    currentRoute = currentRoute
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen()
            }

            composable(Screen.Trips.route) {
                TripsScreen()
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    settingsManager = settingsManager,
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route)
                    }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAccountInfo = { navController.navigate(Screen.AccountInfo.route) },
                    onNavigateToPreferences = { navController.navigate(Screen.Preferences.route) },
                    onNavigateToAbout = { navController.navigate(Screen.About.route) },
                    onNavigateToTerms = { navController.navigate(Screen.TermsConditions.route) }
                )
            }
            composable(Screen.AccountInfo.route) {
                AccountInfoScreen(
                    settingsManager = settingsManager,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Preferences.route) {
                PreferencesScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.About.route) {
                AboutScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.TermsConditions.route) {
                TermsConditionsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentRoute: String? // Recibe la ruta actual como String
) {
    // Creamos una lista solo con las pantallas que van en el menú
    val navigationItems = listOf(Screen.Home, Screen.Trips, Screen.Profile)

    NavigationBar(
        containerColor = NavyBlue,
        contentColor = Color.White
    ) {
        navigationItems.forEach { item ->
            val title = item.titleRes?.let { stringResource(id = it) } ?: ""
            NavigationBarItem(
                icon = { Icon(item.icon!!, contentDescription = title) },
                label = { Text(title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = MapPinRed,
                    indicatorColor = MapPinRed,
                    unselectedIconColor = Color.White,
                    unselectedTextColor = Color.White
                )
            )
        }
    }
}
