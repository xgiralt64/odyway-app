package com.example.odyway.data.repository

import android.util.Log
import com.example.odyway.data.local.dao.TripDao
import com.example.odyway.data.local.entity.TripEntity
import com.example.odyway.data.remote.api.HotelApi
import com.example.odyway.data.remote.dto.HotelDto
import com.example.odyway.data.remote.dto.ReserveRequestDto
import com.example.odyway.data.remote.mapper.toDomain
import com.example.odyway.domain.Hotel
import com.example.odyway.domain.HotelRepository
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HotelRepositoryImpl @Inject constructor(
    private val hotelApi: HotelApi,
    private val tripDao: TripDao,
    private val firebaseAuth: FirebaseAuth
) : HotelRepository {

    private val groupId = "G08"
    private val tag = "HotelRepoImpl"

    override suspend fun checkAvailability(
        startDate: String, endDate: String, city: String?, hotelId: String?
    ): Result<List<Hotel>> {
        return try {
            val response = hotelApi.checkAvailability(
                groupId = groupId,
                startDate = startDate,
                endDate = endDate,
                city = city,
                hotelId = hotelId
            )

            if (response.isSuccessful) {
                val dtoList: List<HotelDto> = response.body()?.availableHotels ?: emptyList<HotelDto>()
                val domainList = dtoList.map { it.toDomain() }
                Result.success(domainList)
            } else {
                Result.failure(Exception("Error al buscar disponibilidad: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión. Revisa tu internet."))
        }
    }

    override suspend fun reserveRoom(
        hotelId: String, roomId: String, startDate: String, endDate: String, guestName: String, guestEmail: String
    ): Result<Unit> {
        return try {
            val request = ReserveRequestDto(hotelId, roomId, startDate, endDate, guestName, guestEmail)
            val response = hotelApi.reserveRoom(groupId, request)

            if (response.isSuccessful) {
                Log.i(tag, "¡Reserva en API completada!")

                val currentUserId = firebaseAuth.currentUser?.uid
                if (currentUserId != null) {
                    val newTrip = TripEntity(
                        id = UUID.randomUUID().toString(),
                        userId = currentUserId,
                        title = "Reserva de Hotel",
                        destination = hotelId,
                        description = "Habitación: $roomId",
                        status = "RESERVED",
                        startDate = LocalDate.parse(startDate),
                        endDate = LocalDate.parse(endDate),
                        budget = 0.0
                    )
                    tripDao.insertTrip(newTrip)
                    Log.i(tag, "Reserva guardada en Room como Trip: ${newTrip.id}")
                }

                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al realizar la reserva: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión al reservar."))
        }
    }
}