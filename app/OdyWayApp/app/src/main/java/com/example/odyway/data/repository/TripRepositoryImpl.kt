package com.example.odyway.data.repository

import android.util.Log
import com.example.odyway.data.local.dao.TripDao
import com.example.odyway.data.local.mapper.toDomain
import com.example.odyway.data.local.mapper.toEntity
import com.example.odyway.domain.AuthRepository
import com.example.odyway.domain.Trip
import com.example.odyway.domain.TripImage
import com.example.odyway.domain.TripRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripRepositoryImpl @Inject constructor(
    private val tripDao: TripDao,
    private val authRepository: AuthRepository, //escuchamos el estado real de Firebase
    private val firebaseAuth: FirebaseAuth //paara obtener el ID al guardar
) : TripRepository {

    private companion object {
        const val TAG = "TripRepositoryImpl"
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAllTrips(): Flow<List<Trip>> {
        //si hay usuario logueado, traemos sus viajes. si no, devolvemos lista vacia
        return authRepository.currentUser.flatMapLatest { user ->
            if (user != null) {
                Log.d(TAG, "Recuperando viajes para usuario real: ${user.id}")
                tripDao.getTripsByUser(user.id).map { entities ->
                    entities.map { it.toDomain() }
                }
            } else {
                flowOf(emptyList())
            }
        }
    }

    override suspend fun getTripById(id: String): Trip? {
        return tripDao.getTripById(id)?.toDomain()
    }

    override suspend fun addTrip(trip: Trip): Result<Unit> {
        return try {
            val currentUserId = firebaseAuth.currentUser?.uid ?: throw Exception("No hay usuario logueado")

            if (trip.title.trim().isEmpty()) {
                Log.w(TAG, "Validación fallida: Título vacío")
                return Result.failure(Exception("El nombre del viaje no puede estar vacío"))
            }

            if (trip.endDate != null && trip.startDate > trip.endDate) {
                Log.w(TAG, "Validación fallida: Fechas incongruentes")
                return Result.failure(Exception("La fecha de inicio no puede ser posterior a la fecha de fin"))
            }

            val duplicateCount = tripDao.countTripsByTitle(currentUserId, trip.title.trim())
            if (duplicateCount > 0) {
                Log.w(TAG, "Validación fallida: Viaje duplicado (${trip.title})")
                return Result.failure(Exception("Ya tienes un viaje guardado con el nombre '${trip.title}'"))
            }

            // ------------

            // Si pasa todas las validaciones, lo guardamos
            tripDao.insertTrip(trip.toEntity(currentUserId))
            Log.i(TAG, "Viaje añadido correctamente a Room: ${trip.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al añadir viaje en Room: ${e.message}")
            Result.failure(Exception("Error guardando el viaje en base de datos"))
        }
    }

    override suspend fun updateTrip(trip: Trip): Result<Unit> {
        return try {
            val currentUserId = firebaseAuth.currentUser?.uid ?: throw Exception("No hay usuario logueado")

            // Validamos Fechas en la actualizacion
            if (trip.endDate != null && trip.startDate > trip.endDate) {
                Log.w(TAG, "Validación fallida al actualizar: Fechas incongruentes")
                return Result.failure(Exception("La fecha de inicio no puede ser posterior a la fecha de fin"))
            }

            // Validar duplicados al actualizar excluyendo el propio viaje que estamos editando
            val existingTrip = tripDao.getTripById(trip.id)
            if (existingTrip != null && existingTrip.title.lowercase() != trip.title.trim().lowercase()) {
                val duplicateCount = tripDao.countTripsByTitle(currentUserId, trip.title.trim())
                if (duplicateCount > 0) {
                    return Result.failure(Exception("Ya tienes otro viaje con el nombre '${trip.title}'"))
                }
            }

            tripDao.updateTrip(trip.toEntity(currentUserId))
            Log.i(TAG, "Viaje actualizado correctamente en Room: ${trip.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar viaje: ${e.message}")
            Result.failure(Exception("Error actualizando el viaje"))
        }
    }

    override suspend fun deleteTrip(id: String): Result<Unit> {
        return try {
            tripDao.deleteTrip(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error borrando el viaje"))
        }
    }

    override suspend fun saveTripImage(tripId: String, localPath: String) {
        // Guardamos la ruta de la imagen en Room
        val entity = com.example.odyway.data.local.entity.TripImageEntity(
            tripId = tripId,
            imagePath = localPath
        )
        tripDao.insertTripImage(entity)
    }

    override fun getTripImages(tripId: String): Flow<List<TripImage>> {
        // Leemos de Room y lo transformamos al modelo de Dominio
        return tripDao.getImagesForTrip(tripId).map { entities ->
            entities.map {
                TripImage(id = it.id, tripId = it.tripId, imagePath = it.imagePath)
            }
        }
    }

    override suspend fun deleteTripImage(imageId: Int) {
        tripDao.deleteTripImage(imageId)
    }
}