package com.example.odyway.data.repository

import android.util.Log
import com.example.odyway.data.local.SettingsManager
import com.example.odyway.data.local.dao.TripDao
import com.example.odyway.data.local.dao.UserAndLogDao
import com.example.odyway.data.local.entity.UserEntity
import com.example.odyway.data.local.mapper.toDomain
import com.example.odyway.data.local.mapper.toEntity
import com.example.odyway.domain.Trip
import com.example.odyway.domain.TripRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripRepositoryImpl @Inject constructor(
    private val tripDao: TripDao,
    private val userAndLogDao: UserAndLogDao,
    private val settingsManager: SettingsManager
) : TripRepository {

    private companion object {
        const val TAG = "TripRepositoryImpl"
    }

    private val currentUserId: String
        get() = settingsManager.username ?: "default_user"

    // ==========================================
    // IMPLEMENTACIÓN DE VIAJES (ROOM)
    // ==========================================

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAllTrips(): Flow<List<Trip>> {
        // MAGIA REACTIVA: Observamos el flujo del usuario. Si cambia de "Invitado" a "Xavi",
        // automáticamente hace una nueva consulta a la base de datos (Room).
        return settingsManager.usernameFlow.flatMapLatest { username ->
            val uid = if (username.isNullOrBlank()) "default_user" else username
            Log.d(TAG, "Recuperando viajes desde Room para usuario: $uid")

            tripDao.getTripsByUser(uid).map { entities ->
                entities.map { it.toDomain() }
            }
        }
    }

    override suspend fun getTripById(id: String): Trip? {
        Log.d(TAG, "Recuperando viaje por ID: $id")
        return tripDao.getTripById(id)?.toDomain()
    }

    override suspend fun addTrip(trip: Trip): Result<Unit> {
        Log.d(TAG, "Intentando añadir viaje: ${trip.title}")
        return try {
            // HACK TEMPORAL: Asegurar que el usuario existe en DB para la Foreign Key
            val user = userAndLogDao.getUserById(currentUserId)
            if (user == null) {
                userAndLogDao.insertUser(UserEntity(currentUserId, currentUserId, currentUserId, "email@test.com", null, 0L))
            }

            tripDao.insertTrip(trip.toEntity(currentUserId))
            Log.i(TAG, "Viaje añadido correctamente a Room: ${trip.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al añadir viaje en Room: ${e.message}")
            Result.failure(Exception("Error guardando el viaje en base de datos"))
        }
    }

    override suspend fun updateTrip(trip: Trip): Result<Unit> {
        Log.d(TAG, "Intentando actualizar viaje: ${trip.id}")
        return try {
            tripDao.updateTrip(trip.toEntity(currentUserId))
            Log.i(TAG, "Viaje actualizado correctamente en Room: ${trip.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar viaje en Room: ${e.message}")
            Result.failure(Exception("Error actualizando el viaje"))
        }
    }

    override suspend fun deleteTrip(id: String): Result<Unit> {
        Log.d(TAG, "Intentando borrar viaje: $id")
        return try {
            tripDao.deleteTrip(id)
            Log.i(TAG, "Viaje borrado correctamente de Room")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al borrar viaje en Room: ${e.message}")
            Result.failure(Exception("Error borrando el viaje"))
        }
    }
}