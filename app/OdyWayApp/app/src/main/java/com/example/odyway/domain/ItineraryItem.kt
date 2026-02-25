package com.example.odyway.domain

import java.time.LocalDateTime

data class ItineraryItem(
    val id: String,
    val title: String,
    val description: String,
    val scheduledTime: LocalDateTime
) {

    fun isPast(): Boolean {
        return scheduledTime.isBefore(LocalDateTime.now())
    }
}