package com.example.odyway.domain

import java.time.LocalDate

/**
 * Represents a travel plan created by a user.
 */
data class Trip(
    val id: String,
    val userId: String,
    val title: String,
    val description: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val budget: Double,
    val destinations: List<String>,
    val activities: List<Activity>,
    val itinerary: List<ItineraryItem>,
    val images: List<Image>
) {

    /**
     * Calculates total duration of the trip in days.
     */
    fun getDurationInDays(): Long {
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate)
    }

    /**
     * Calculates remaining budget after planned activities.
     */
    fun getRemainingBudget(): Double {
        val totalActivityCost = activities.sumOf { it.cost }
        return budget - totalActivityCost
    }

    /**
     * Calculates total cost of the trip.
     */
    fun getTotalCost(): Double {
        return activities.sumOf { it.cost }
    }

    /**
     * Checks if the trip is currently active.
     */
    fun isTripActive(currentDate: LocalDate = LocalDate.now()): Boolean {
        return currentDate.isAfter(startDate.minusDays(1)) &&
                currentDate.isBefore(endDate.plusDays(1))
    }

    /**
     * Future feature: calculate optimization of daily spending.
     */
    fun optimizeBudgetDistribution() {
        // @TODO Implement smart AI-based budget distribution algorithm
    }

    /**
     * Future feature: suggest best transport options between destinations.
     */
    fun suggestTransportOptions() {
        // @TODO Integrate external API for transport suggestions
    }
}