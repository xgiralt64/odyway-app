package com.example.odyway.data.local.mapper

import com.example.odyway.data.local.entity.ItineraryItemEntity
import com.example.odyway.domain.ItineraryItem

fun ItineraryItemEntity.toDomain(): ItineraryItem {
    return ItineraryItem(
        id = this.id,
        tripId = this.tripId,
        date = this.date,
        time = this.time,
        title = this.title,
        description = this.description,
        location = this.location,
        isCompleted = this.isCompleted,
        category = this.category
    )
}

fun ItineraryItem.toEntity(): ItineraryItemEntity {
    return ItineraryItemEntity(
        id = this.id,
        tripId = this.tripId,
        date = this.date,
        time = this.time,
        title = this.title,
        description = this.description,
        location = this.location,
        isCompleted = this.isCompleted,
        category = this.category
    )
}