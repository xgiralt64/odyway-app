package com.example.odyway.data.local.mapper

import com.example.odyway.data.local.entity.TripEntity
import com.example.odyway.domain.Trip

fun TripEntity.toDomain(): Trip {
    return Trip(
        id = this.id,
        userId = this.userId,
        title = this.title,
        destination = this.destination,
        description = this.description,
        status = this.status,
        startDate = this.startDate,
        endDate = this.endDate,
        budget = this.budget,
        activities = emptyList(),
        itinerary = emptyList(),
        images = emptyList()
    )
}

fun Trip.toEntity(currentUserId: String): TripEntity {
    return TripEntity(
        id = this.id,
        userId = currentUserId,
        title = this.title,
        destination = this.destination,
        description = this.description,
        status = this.status,
        startDate = this.startDate,
        endDate = this.endDate,
        budget = this.budget
    )
}