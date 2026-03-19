package com.example.odyway

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.example.odyway.data.local.SettingsManager
import com.example.odyway.ui.theme.OdyWayTheme
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            val settingsManager = remember { SettingsManager(context) }
            
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
                    NavGraph()
                }
            }
        }
    }
}
