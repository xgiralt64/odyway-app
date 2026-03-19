package com.example.odyway

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import com.example.odyway.data.local.SettingsManager
import com.example.odyway.data.repository.TripRepositoryImpl
import com.example.odyway.ui.theme.OdyWayTheme
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // --- INICIO DE LA PRUEBA DEL PASO 1 ---
        val testRepository = TripRepositoryImpl()

        // Lanzamos una pequeña corrutina porque Flow necesita recolectarse
        lifecycleScope.launch {
            testRepository.getAllTrips().collect { trips ->
                Log.w("PRUEBA_PASO1", "¡Funciona! Hay ${trips.size} viajes en la memoria.")
                trips.forEach { trip ->
                    Log.w("PRUEBA_PASO1", "-> Viaje: ${trip.title} (Destino: ${trip.destination})")
                }
            }
        }

        setContent {
            val context = LocalContext.current
            val settingsManager = remember { SettingsManager(context) }

            val tripRepository = remember { TripRepositoryImpl() }
            
            // Observem tant el mode fosc com l'idioma
            val isDarkMode by settingsManager.isDarkModeFlow.collectAsState()
            val languageCode by settingsManager.languageFlow.collectAsState()

            // Preparem la Locale i la Configuració
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            
            val config = Configuration(LocalConfiguration.current)
            config.setLocale(locale)
            
            // Creem un context localitzat perquè stringResource utilitzi l'idioma correcte
            val localizedContext = context.createConfigurationContext(config)
            
            CompositionLocalProvider(
                LocalConfiguration provides config,
                LocalContext provides localizedContext
            ) {
                OdyWayTheme(darkTheme = isDarkMode) {
                    NavGraph(
                        settingsManager = settingsManager,
                        tripRepository = tripRepository
                    )

                }
            }
        }
    }
}
