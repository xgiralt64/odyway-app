package com.example.odyway.domain

data class Preferences(
    val userId: String,
    val isDarkMode: Boolean,
    val notificationsEnabled: Boolean,
    val preferredLanguage: String
)