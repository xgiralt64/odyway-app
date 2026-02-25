package com.example.odyway.domain

import java.time.LocalDateTime

data class Activity(
    val id: String,
    val name: String,
    val description: String,
    val location: String,
    val dateTime: LocalDateTime,
    val cost: Double
) {

    fun isFree(): Boolean {
        return cost == 0.0
    }

    fun getFormattedCost(): String {
        return "€%.2f".format(cost)
    }

    fun validateSchedule() {
        // @TODO Validate activity does not overlap with other activities
    }
}