package com.example.odyway.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "trips",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE // Si borras al usuario, se borran sus viajes
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class TripEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val destination: String,
    val description: String,
    val status: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val budget: Double
)