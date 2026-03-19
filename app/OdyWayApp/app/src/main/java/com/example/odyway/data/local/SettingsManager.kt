package com.example.odyway.data.local

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("odyway_settings", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_LANGUAGE = "language"
        private const val KEY_USERNAME = "username"
        private const val KEY_BIRTH_DATE = "birth_date"
        private const val KEY_DARK_MODE = "dark_mode"
    }

    // --- Mètodes genèrics ---

    fun saveBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun saveString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String? = null): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    // --- Mètodes específics per a les preferències de l'app ---

    // Idioma
    var language: String?
        get() = getString(KEY_LANGUAGE, "ca")
        set(value) = saveString(KEY_LANGUAGE, value ?: "ca")

    // Nom d'usuari
    var username: String?
        get() = getString(KEY_USERNAME, "")
        set(value) = saveString(KEY_USERNAME, value ?: "")

    // Data de naixement
    var birthDate: String?
        get() = getString(KEY_BIRTH_DATE, "")
        set(value) = saveString(KEY_BIRTH_DATE, value ?: "")

    // Dark Mode
    var isDarkMode: Boolean
        get() = getBoolean(KEY_DARK_MODE, false)
        set(value) = saveBoolean(KEY_DARK_MODE, value)
}