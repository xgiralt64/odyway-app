package com.example.odyway

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.*
import androidx.navigation.compose.NavHost
import com.example.odyway.ui.screens.*
import com.example.odyway.ui.theme.CyanBlue
import com.example.odyway.ui.theme.MapPinRed
import com.example.odyway.ui.theme.NavyBlue
import com.example.odyway.ui.theme.White

// sealed class
sealed class Screen(
    val route: String,
    val title: String? = null,
    val icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    data object Home : Screen("home", "Home", Icons.Filled.Home)
    data object Trips : Screen("trips", "Trips", Icons.Filled.Place)
    data object Profile : Screen("profile", "Profile", Icons.Filled.Person)
    data object Splash : Screen("splash")
}

@Composable
fun NavGraph() {
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
            startDestination = Screen.Splash.route, // Usar la constante!
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Home.route) {
                HomeScreen()
            }
            composable(Screen.Trips.route) {
                Text("Pantalla de Viajes")
            }
            composable(Screen.Profile.route) {
                Text("Pantalla de Cuenta")
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
            NavigationBarItem(
                icon = { Icon(item.icon!!, contentDescription = item.title) },
                label = { Text(item.title!!) },
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

@Preview
@Composable
fun Preview() {
    NavGraph()
}
