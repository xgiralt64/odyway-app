package com.example.odyway.domain

import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class Trip(
    val id: String,
    val userId: String,
    val title: String,
    val destination: String,
    val status: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val budget: Double,
    val activities: List<Activity> = emptyList(),
    val itinerary: List<ItineraryItem> = emptyList(), // ¡AQUÍ VUELVE EL ITINERARIO!
    val images: List<GalleryImage> = emptyList()
) {
    fun getDurationInDays(): Long {
        return ChronoUnit.DAYS.between(startDate, endDate)
    }

    fun getTotalCost(): Double {
        return activities.sumOf { it.cost }
    }

    fun getRemainingBudget(): Double {
        return budget - getTotalCost()
    }

    fun isTripActive(currentDate: LocalDate = LocalDate.now()): Boolean {
        return currentDate.isAfter(startDate.minusDays(1)) &&
                currentDate.isBefore(endDate.plusDays(1))
    }
}