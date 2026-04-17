package com.example.odyway.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(
    tableName = "itinerary_items",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE // Si borras el viaje, se borra su itinerario
        )
    ],
    indices = [Index(value = ["tripId"])]
)
data class ItineraryItemEntity(
    @PrimaryKey val id: String,
    val tripId: String,
    val date: LocalDate,
    val time: LocalTime,
    val title: String,
    val description: String,
    val location: String,
    val isCompleted: Boolean,
    val category: String
)