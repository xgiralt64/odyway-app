package com.example.odyway.ui.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.odyway.data.SettingsManager
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager
) : ViewModel() {

    // exponemos los flujos para que la UI los lea
    val isDarkMode: StateFlow<Boolean> = settingsManager.isDarkModeFlow
    val languageCode: StateFlow<String> = settingsManager.languageFlow
    val username: StateFlow<String> = settingsManager.usernameFlow
    val birthDate: StateFlow<Long> = settingsManager.birthDateFlow

    // funciones para que la UI cambie los valores
    fun updateDarkMode(isDark: Boolean) {
        settingsManager.isDarkMode = isDark
    }

    fun updateLanguage(code: String) {
        settingsManager.language = code
    }

    fun updateUsername(newName: String) {
        settingsManager.username = newName
    }

    fun updateBirthDate(newDate: Long) {
        settingsManager.birthDate = newDate
    }
}