package com.example.odyway.data.repository

import android.util.Log
import com.example.odyway.data.fakeDB.FakeTripDataSource
import com.example.odyway.domain.ItineraryItem
import com.example.odyway.domain.Trip
import com.example.odyway.domain.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementación del repositorio conectada a la base de datos en memoria.
 * Retorna Flow para lectura y Result para las escrituras, logueando cada acción.
 */
class TripRepositoryImpl(
    private val dataSource: FakeTripDataSource = FakeTripDataSource
) : TripRepository {

    private companion object {
        const val TAG = "TripRepositoryImpl"
    }

    // ==========================================
    // IMPLEMENTACIÓN DE VIAJES
    // ==========================================

    override fun getAllTrips(): Flow<List<Trip>> {
        Log.d(TAG, "Recuperando todos los viajes")
        return dataSource.trips
    }

    override suspend fun getTripById(id: String): Trip? {
        return dataSource.getTripById(id)
    }

    override suspend fun addTrip(trip: Trip): Result<Unit> {
        Log.d(TAG, "Intentando añadir viaje: ${trip.title}")
        val success = dataSource.addTrip(trip)
        return if (success) {
            Log.i(TAG, "Viaje añadido correctamente: ${trip.id}")
            Result.success(Unit)
        } else {
            Log.e(TAG, "Error al añadir: El viaje con ID ${trip.id} ya existe")
            Result.failure(Exception("El viaje ya existe"))
        }
    }

    override suspend fun updateTrip(trip: Trip): Result<Unit> {
        Log.d(TAG, "Intentando actualizar viaje: ${trip.id}")
        val success = dataSource.updateTrip(trip)
        return if (success) {
            Log.i(TAG, "Viaje actualizado correctamente: ${trip.id}")
            Result.success(Unit)
        } else {
            Log.e(TAG, "Error al actualizar: Viaje no encontrado")
            Result.failure(Exception("Viaje no encontrado"))
        }
    }

    override suspend fun deleteTrip(id: String): Result<Unit> {
        Log.d(TAG, "Intentando borrar viaje: $id")
        val success = dataSource.deleteTrip(id)
        return if (success) {
            Log.i(TAG, "Viaje y sus actividades borrados correctamente")
            Result.success(Unit)
        } else {
            Log.e(TAG, "Error al borrar: Viaje no encontrado")
            Result.failure(Exception("Viaje no encontrado"))
        }
    }

    // ==========================================
    // IMPLEMENTACIÓN DE ITINERARIO
    // ==========================================

    override fun getItineraryForTrip(tripId: String): Flow<List<ItineraryItem>> {
        Log.d(TAG, "Recuperando itinerario para el viaje: $tripId")
        return dataSource.itinerary.map { list ->
            list.filter { it.tripId == tripId }.sortedBy { it.date.atTime(it.time) }
        }
    }

    override suspend fun addItineraryItem(item: ItineraryItem): Result<Unit> {
        val success = dataSource.addItineraryItem(item)
        return if (success) {
            Log.i(TAG, "Actividad añadida: ${item.title}")
            Result.success(Unit)
        } else {
            Result.failure(Exception("La actividad ya existe"))
        }
    }

    override suspend fun updateItineraryItem(item: ItineraryItem): Result<Unit> {
        val success = dataSource.updateItineraryItem(item)
        return if (success) Result.success(Unit) else Result.failure(Exception("Actividad no encontrada"))
    }

    override suspend fun deleteItineraryItem(itemId: String): Result<Unit> {
        val success = dataSource.deleteItineraryItem(itemId)
        return if (success) Result.success(Unit) else Result.failure(Exception("Actividad no encontrada"))
    }
}