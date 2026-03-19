package com.example.odyway

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.odyway.data.local.SettingsManager
import com.example.odyway.ui.theme.OdyWayTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = androidx.compose.ui.platform.LocalContext.current
            val settingsManager = remember { SettingsManager(context) }
            val isDarkMode by settingsManager.isDarkModeFlow.collectAsState()

            OdyWayTheme(darkTheme = isDarkMode) {
                NavGraph()
            }
        }
    }
}
