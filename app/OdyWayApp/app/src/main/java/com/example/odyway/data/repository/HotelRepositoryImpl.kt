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
import com.example.odyway.domain.Reservation
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
        hotelId: String, roomId: String, startDate: String, endDate: String, guestName: String, guestEmail: String,
        hotelName: String, roomType: String, price: Double // NUEVO
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
                        title = "Reserva: $hotelName", // Guardamos el nombre del hotel
                        destination = hotelId, // O la ciudad si la pasas
                        description = "Habitación: $roomType", // Guardamos el tipo
                        status = "RESERVED",
                        startDate = LocalDate.parse(startDate),
                        endDate = LocalDate.parse(endDate),
                        budget = price // Guardamos el precio real
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


    //Obtener y Cancelar Reservas (API)

    override suspend fun getGroupReservations(guestEmail: String): Result<List<Reservation>> {
        return try {
            val response = hotelApi.getReservations(groupId, guestEmail)

            if (response.isSuccessful) {
                // Obtenemos el contenedor DTO
                val body = response.body()
                // Extraemos la lista de reservas que hay dentro
                val dtoList = body?.reservations ?: emptyList()

                // Mapeo completo
                val domainList = dtoList.map { dto ->
                    Reservation(
                        id = dto.id,
                        hotel_id = dto.hotelId,
                        room_id = dto.roomId,
                        start_date = dto.startDate,
                        end_date = dto.endDate,
                        guest_name = dto.guestName,
                        guest_email = dto.guestEmail,
                        // Mapeamos el hotel para obtener su nombre e imageUrl
                        hotel = dto.hotel?.let { hDto ->
                            Hotel(
                                id = hDto.id,
                                name = hDto.name,
                                address = hDto.address,
                                rating = hDto.rating,
                                imageUrl = hDto.imageUrl,
                                rooms = emptyList()
                            )
                        },
                        // Mapeamos la habitación por si necesitas el tipo o precio
                        room = dto.room?.let { rDto ->
                            com.example.odyway.domain.Room(
                                id = rDto.id,
                                roomType = rDto.roomType,
                                price = rDto.price,
                                images = rDto.images
                            )
                        }
                    )
                }
                Result.success(domainList)
            } else {
                Result.failure(Exception("Error de la API: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Fallo crítico al procesar el JSON de reservas: ${e.message}", e)
            Result.failure(Exception("Error al procesar los datos de la API."))
        }
    }

    override suspend fun cancelReservationById(resId: String): Result<Unit> {
        return try {
            // Borramos la reserva en la API remota
            val response = hotelApi.cancelReservationById(resId)

            if (response.isSuccessful) {
                Log.i(tag, "Reserva $resId cancelada correctamente en la API.")
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al cancelar la reserva en la API."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión al cancelar la reserva."))
        }
    }
}