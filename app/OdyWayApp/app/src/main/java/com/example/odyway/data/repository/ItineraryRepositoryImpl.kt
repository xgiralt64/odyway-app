package com.example.odyway.data.repository

import android.util.Log
import com.example.odyway.data.local.dao.ItineraryDao
import com.example.odyway.data.local.mapper.toDomain
import com.example.odyway.data.local.mapper.toEntity
import com.example.odyway.domain.ItineraryItem
import com.example.odyway.domain.ItineraryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItineraryRepositoryImpl @Inject constructor(
    private val itineraryDao: ItineraryDao
) : ItineraryRepository {

    private companion object {
        const val TAG = "ItineraryRepoImpl"
    }

    // ==========================================
    // IMPLEMENTACIÓN DE ITINERARIO (ROOM)
    // ==========================================

    override fun getItineraryForTrip(tripId: String): Flow<List<ItineraryItem>> {
        Log.d(TAG, "Recuperando itinerario para el viaje: $tripId")
        return itineraryDao.getItineraryForTrip(tripId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addItineraryItem(item: ItineraryItem): Result<Unit> {
        return try {
            itineraryDao.insertItineraryItem(item.toEntity())
            Log.i(TAG, "Actividad añadida: ${item.title}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al añadir actividad: ${e.message}")
            Result.failure(Exception("Error al añadir la actividad en base de datos"))
        }
    }

    override suspend fun updateItineraryItem(item: ItineraryItem): Result<Unit> {
        return try {
            itineraryDao.updateItineraryItem(item.toEntity())
            Log.i(TAG, "Actividad actualizada: ${item.title}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar actividad: ${e.message}")
            Result.failure(Exception("Actividad no encontrada o error de DB"))
        }
    }

    override suspend fun deleteItineraryItem(itemId: String): Result<Unit> {
        return try {
            itineraryDao.deleteItineraryItem(itemId)
            Log.i(TAG, "Actividad borrada: $itemId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al borrar actividad: ${e.message}")
            Result.failure(Exception("Actividad no encontrada o error de DB"))
        }
    }
}