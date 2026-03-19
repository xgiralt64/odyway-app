package com.example.odyway.domain

import java.time.LocalDate
import java.time.LocalTime

/**
 * Represents a scheduled event within a Trip's itinerary (Day & Time).
 */
data class ItineraryItem(
    val id: String,
    val tripId: String,
    val date: LocalDate, // Añadido para poder agrupar por días
    val time: LocalTime,
    val title: String,
    val description: String,
    val location: String,
    val isCompleted: Boolean = false
) {
    fun isValid(): Boolean {
        // @TODO Implement strict validation for time formats and titles
        return false
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