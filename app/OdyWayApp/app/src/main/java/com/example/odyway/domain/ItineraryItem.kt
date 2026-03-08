package com.example.odyway.domain

import java.time.LocalDate

/**
 * Represents a scheduled event within a Trip's itinerary (Day & Time).
 */
data class ItineraryItem(
    val id: String,
    val tripId: String,
    val date: LocalDate, // Añadido para poder agrupar por días
    val time: String,
    val title: String,
    val location: String,
    val isCompleted: Boolean = false
) {
    fun isValid(): Boolean {
        // @TODO Implement strict validation for time formats and titles
        return id.isNotBlank() && time.isNotBlank() && title.isNotBlank()
    }

    fun save() {
        // @TODO Implement persistence via ItineraryRepository
    }

    fun delete() {
        // @TODO Implement deletion logic in ItineraryRepository
    }

    fun toggleComplete() {
        // @TODO Update isCompleted state and persist
    }
}