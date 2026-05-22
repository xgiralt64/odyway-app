package com.example.odyway.domain

import kotlinx.coroutines.flow.Flow

/**
 * Interfaz que define las operaciones CRUD para Viajes y Actividades.
 * Se utilizan funciones 'suspend' para prepararlo para la futura base de datos real,
 * y 'Result' para devolver si la operación fue un éxito o hubo un error de validación.
 */
interface TripRepository {

    // Operaciones de Viajes Trips
    fun getAllTrips(): Flow<List<Trip>>
    suspend fun getTripById(id: String): Trip?
    suspend fun addTrip(trip: Trip): Result<Unit>
    suspend fun updateTrip(trip: Trip): Result<Unit>
    suspend fun deleteTrip(id: String): Result<Unit>
    //imagenes del viaje
    suspend fun saveTripImage(tripId: String, localPath: String)
    fun getTripImages(tripId: String): kotlinx.coroutines.flow.Flow<List<TripImage>>
    suspend fun deleteTripImage(imageId: Int)
}
