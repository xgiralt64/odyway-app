package com.example.odyway.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("odyway_settings", Context.MODE_PRIVATE)

    private val _isDarkModeFlow = MutableStateFlow(getBoolean(KEY_DARK_MODE, false))
    val isDarkModeFlow: StateFlow<Boolean> = _isDarkModeFlow

    private val _languageFlow = MutableStateFlow(getString(KEY_LANGUAGE, "ca") ?: "ca")
    val languageFlow: StateFlow<String> = _languageFlow

    //Al final hem hagut de crear una referencia forta perque no ens funcionava ja que el Garbage Collector l'elimini
    //Canviar si trobem una millor solució
    private val preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        when (key) {
            KEY_DARK_MODE -> {
                _isDarkModeFlow.value = prefs.getBoolean(key, false)
            }
            KEY_LANGUAGE -> {
                _languageFlow.value = prefs.getString(key, "ca") ?: "ca"
            }
        }
    }

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

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

    // --- Mètodes específics ---

    var language: String?
        get() = getString(KEY_LANGUAGE, "ca")
        set(value) = saveString(KEY_LANGUAGE, value ?: "ca")

    var username: String?
        get() = getString(KEY_USERNAME, "")
        set(value) = saveString(KEY_USERNAME, value ?: "")

    var birthDate: String?
        get() = getString(KEY_BIRTH_DATE, "")
        set(value) = saveString(KEY_BIRTH_DATE, value ?: "")

    var isDarkMode: Boolean
        get() = getBoolean(KEY_DARK_MODE, false)
        set(value) = saveBoolean(KEY_DARK_MODE, value)
}
