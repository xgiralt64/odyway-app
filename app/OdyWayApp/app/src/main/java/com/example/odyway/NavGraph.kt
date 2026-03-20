package com.example.odyway

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.*
import androidx.navigation.compose.NavHost
import com.example.odyway.data.local.SettingsManager
import com.example.odyway.ui.screens.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.odyway.domain.TripRepository
import com.example.odyway.ui.theme.OdyWayTheme
import com.example.odyway.ui.viewmodels.TripViewModel
import com.example.odyway.ui.viewmodels.TripViewModelFactory

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
fun NavGraph(
    settingsManager: SettingsManager,
    tripRepository: TripRepository
) {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Usamos las constantes de tu sealed class para evitar errores de tipeo
    val routesWithBottomBar = listOf(Screen.Home.route, Screen.Trips.route, Screen.Profile.route)

    val tripViewModel: TripViewModel = viewModel(
        factory = TripViewModelFactory(tripRepository)
    )

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
                // Le pasamos el cerebro a la Home
                HomeScreen(tripViewModel = tripViewModel)
            }


            //pantalla de lista de viajes
            composable(Screen.Trips.route) {
                TripsScreen(
                    tripViewModel = tripViewModel,
                    onTripClick = { tripId ->
                        // Cuando clicamos en una tarjeta, navegamos al detalle
                        navController.navigate("trip_detail/$tripId")
                    },
                    onCreateTripClick = {
                        // TODO: En el próximo paso navegaremos a AddTripScreen
                    }
                )
            }

            // Detalles del viaje
            composable("trip_detail/{tripId}") { backStackEntry ->
                val tripId = backStackEntry.arguments?.getString("tripId")

                LaunchedEffect(tripId) {
                    tripId?.let { tripViewModel.loadItineraryForTrip(it) }
                }

                TripDetailScreen(
                    tripViewModel = tripViewModel,
                    onModifyItineraryClick = { tripId ->
                        navController.navigate("itinerary_edit/$tripId")
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            //La pantalla del Planificador de Itinerario
            composable("itinerary_edit/{tripId}") { backStackEntry ->
                val tripId = backStackEntry.arguments?.getString("tripId") ?: return@composable

                ItineraryEditScreen(
                    tripId = tripId,
                    tripViewModel = tripViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
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
    currentRoute: String?
) {
    val navigationItems = listOf(Screen.Home, Screen.Trips, Screen.Profile)

    NavigationBar(
        // El fondo será tu color primario (NavyBlue en tu tema)
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
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
                    // Icono seleccionado (blanco para que contraste con la burbuja roja)
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    // Texto seleccionado (rojo para resaltar)
                    selectedTextColor = MaterialTheme.colorScheme.error,
                    // La burbuja de fondo del icono seleccionado (rojo)
                    indicatorColor = MaterialTheme.colorScheme.error,
                    // Iconos y textos no seleccionados (blancos pero un poco transparentes)
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                )
            )
        }
    }
}
//
//@Preview(showBackground = true, name = "Profile Mode Light")
//@Composable
//fun NavScreenPreviewLight() {
//    OdyWayTheme(darkTheme = false) {
//        BottomNavigationBar{}
//    }
//}
//
//@Preview(showBackground = true, name = "Profile Mode Dark")
//@Composable
//fun NavScreenPreviewDark() {
//    val context = LocalContext.current
//    OdyWayTheme(darkTheme = true) {
//        BottomNavigationBar{}
//    }
//}
