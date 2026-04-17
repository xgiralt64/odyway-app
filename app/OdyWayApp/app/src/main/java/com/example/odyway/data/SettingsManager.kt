package com.example.odyway.data

import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsManager @Inject constructor(private val sharedPreferences: SharedPreferences) {

    private val _isDarkModeFlow = MutableStateFlow(getBoolean(KEY_DARK_MODE, false))
    val isDarkModeFlow: StateFlow<Boolean> = _isDarkModeFlow

    private val _languageFlow = MutableStateFlow(getString(KEY_LANGUAGE, "ca") ?: "ca")
    val languageFlow: StateFlow<String> = _languageFlow

    private val _usernameFlow = MutableStateFlow(getString(KEY_USERNAME, "Xavi") ?: "Xavi")
    val usernameFlow: StateFlow<String> = _usernameFlow

    private val _birthDateFlow = MutableStateFlow(getLong(KEY_BIRTH_DATE, 0L))
    val birthDateFlow: StateFlow<Long> = _birthDateFlow

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
            KEY_USERNAME -> {
                _usernameFlow.value = prefs.getString(key, "Xavi") ?: "Xavi"
            }
            KEY_BIRTH_DATE -> {
                _birthDateFlow.value = getLong(key, 0L)
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
        return try {
            sharedPreferences.getBoolean(key, defaultValue)
        } catch (e: ClassCastException) {
            defaultValue
        }
    }

    fun saveString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String? = null): String? {
        return try {
            sharedPreferences.getString(key, defaultValue)
        } catch (e: ClassCastException) {
            defaultValue
        }
    }

    fun saveLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }

    fun getLong(key: String, defaultValue: Long): Long {
        return try {
            sharedPreferences.getLong(key, defaultValue)
        } catch (e: ClassCastException) {
            // Se ve que en algún momento se guardó como String o hay un conflicto de tipos
            val value = sharedPreferences.all[key]
            if (value is String) {
                value.toLongOrNull() ?: defaultValue
            } else {
                defaultValue
            }
        }
    }

    // --- Mètodes específics ---

    var language: String?
        get() = getString(KEY_LANGUAGE, "ca")
        set(value) = saveString(KEY_LANGUAGE, value ?: "ca")

    var username: String?
        get() = getString(KEY_USERNAME, "Xavi")
        set(value) = saveString(KEY_USERNAME, value ?: "Xavi")

    var birthDate: Long
        get() = getLong(KEY_BIRTH_DATE, 0L)
        set(value) = saveLong(KEY_BIRTH_DATE, value)

    var isDarkMode: Boolean
        get() = getBoolean(KEY_DARK_MODE, false)
        set(value) = saveBoolean(KEY_DARK_MODE, value)
}