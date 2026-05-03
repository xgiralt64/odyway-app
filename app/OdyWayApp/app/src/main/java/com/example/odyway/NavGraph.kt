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
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.*
import androidx.navigation.compose.NavHost
import com.example.odyway.ui.screens.*
import androidx.navigation.navArgument
import com.example.odyway.ui.viewmodels.TripViewModel

import androidx.hilt.navigation.compose.hiltViewModel
import com.example.odyway.ui.viewmodels.AuthViewModel

// sealed class amb resource IDs per a la traducció
sealed class Screen(
    val route: String,
    val titleRes: Int? = null,
    val icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    data object Home : Screen("home", R.string.nav_home, Icons.Filled.Home)
    data object Login : Screen("login", R.string.login_button, Icons.Filled.Login)
    data object Register : Screen("register")
    data object RecoverPassword : Screen("recover_password")
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
fun NavGraph() {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    //crea el ViewModel y le inyecta el Repositorio automaticamente
    val tripViewModel: TripViewModel = hiltViewModel()

    // Instanciamos el AuthViewModel aquí para poder cerrar sesión
    val authViewModel: AuthViewModel = hiltViewModel()

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
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    },
                    onNavigateToRecoverPassword = {
                        navController.navigate(Screen.RecoverPassword.route)
                    }
                )
            }

            composable(Screen.Register.route) {
                // mostrar el Toast
                val context = androidx.compose.ui.platform.LocalContext.current

                RegisterScreen(
                    onRegisterSuccess = {
                        //mostramos un mensaje nativo de Android
                        android.widget.Toast.makeText(
                            context,
                            "Registro exitoso. ¡Revisa tu email para verificar la cuenta!",
                            android.widget.Toast.LENGTH_LONG
                        ).show()

                        // Le mandamos de vuelta a la pantalla de Login
                        navController.popBackStack()
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.RecoverPassword.route) {
                RecoverPasswordScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    tripViewModel = tripViewModel,
                    onCreateTripClick = { navController.navigate("add_edit_trip") }

                )
            }


            //pantalla de lista de viajes
            composable(Screen.Trips.route) {
                TripsScreen(
                    tripViewModel = tripViewModel,
                    onTripClick = { tripId ->
                        navController.navigate("trip_detail/$tripId")
                    },
                    onCreateTripClick = { navController.navigate("add_edit_trip") }, // ¡Corregido el nombre!
                    onEditTripClick = { tripId -> navController.navigate("add_edit_trip?tripId=$tripId") }
                )
            }

            // La pantalla para Crear Viajes
            composable(
                route = "add_edit_trip?tripId={tripId}",
                arguments = listOf(navArgument("tripId") { nullable = true; defaultValue = null })
            ) { backStackEntry ->
                val tripId = backStackEntry.arguments?.getString("tripId")

                AddEditTripScreen(
                    tripId = tripId,
                    tripViewModel = tripViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Detalles del viaje
            composable("trip_detail/{tripId}") { backStackEntry ->
                // Ponemos un fallback seguro por si el ID viene nulo
                val tripId = backStackEntry.arguments?.getString("tripId") ?: return@composable

                LaunchedEffect(tripId) {
                    tripViewModel.loadItineraryForTrip(tripId)
                }

                TripDetailScreen(
                    tripId = tripId, // <-- AÑADIMOS ESTA LÍNEA AQUÍ
                    tripViewModel = tripViewModel,
                    onModifyItineraryClick = {
                        navController.navigate("itinerary_edit/$tripId")
                    },
                    onNavigateBack = { navController.popBackStack() },
                    onEditTripClick = { navController.navigate("add_edit_trip?tripId=$tripId") }
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
                    onNavigateToTerms = { navController.navigate(Screen.TermsConditions.route) },
                    onLogoutClick = {
                        // Llamamos a la función de Firebase
                        authViewModel.logout(
                            onSuccess = {
                                // Navegamos al Login y borramos todo el historial (BackStack)
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }
                )
            }
            composable(Screen.AccountInfo.route) {
                AccountInfoScreen(
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
