package com.example.odyway.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "trip_images",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE // Si se borra el viaje, se borran sus fotos de la BBDD
        )
    ],
    indices = [Index("tripId")] // Optimiza las búsquedas
)
data class TripImageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tripId: String,
    val imagePath: String // Aquí guardaremos la ruta segura de la imagen copiada
)