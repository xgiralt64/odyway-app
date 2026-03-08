package com.example.odyway.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = NavyBlue,           // Mantenemos la barra superior azul oscura
    onPrimary = White,            // Texto sobre la barra superior
    secondary = CyanBlue,
    tertiary = GoldOrange,
    background = DarkNavy,        // Fondo casi negro
    onBackground = White,         // Texto principal en modo oscuro
    surface = NavyBlue,           // Las tarjetas serán azul oscuro
    onSurface = White,            // Texto dentro de las tarjetas
    error = MapPinRed
)

private val LightColorScheme = lightColorScheme(
    primary = NavyBlue,           // Barra superior azul oscura
    onPrimary = White,            // Texto sobre la barra superior
    secondary = CyanBlue,
    tertiary = GoldOrange,
    background = BackgroundLight, // Fondo gris claro
    onBackground = NavyBlue,      // Texto principal en modo claro
    surface = White,              // Las tarjetas serán blancas
    onSurface = NavyBlue,         // Texto dentro de las tarjetas
    error = MapPinRed
)

@Composable
fun OdyWayTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // IMPORTANTE: Puesto en false para forzar NUESTROS colores y no los del sistema
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Asegúrate de tener tu archivo Type.kt
        content = content
    )
}