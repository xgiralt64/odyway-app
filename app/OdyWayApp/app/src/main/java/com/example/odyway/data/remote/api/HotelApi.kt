package com.example.odyway.data.remote.api

import com.example.odyway.data.remote.dto.AvailabilityResponseDto
import com.example.odyway.data.remote.dto.HotelDto
import com.example.odyway.data.remote.dto.ReserveRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface HotelApi {

    //Buscar disponibilidad de hoteles
    @GET("/hotels/{group_id}/availability")
    suspend fun checkAvailability(
        @Path("group_id") groupId: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("city") city: String? = null, // Opcional, e.g. "barcelona"
        @Query("hotel_id") hotelId: String? = null
    ): Response<AvailabilityResponseDto>

    //Hacer una reserva
    @POST("/hotels/{group_id}/reserve")
    suspend fun reserveRoom(
        @Path("group_id") groupId: String,
        @Body request: ReserveRequestDto
    ): Response<Unit>

    // Cancelar una reserva por su ID (Res Id)
    @DELETE("/reservations/{res_id}")
    suspend fun cancelReservationById(
        @Path("res_id") reservationId: String
    ): Response<Unit>

    // (Opcionaal) Obtener todas las reservas de un email no sabemos si lo vamos a implementar
    @GET("/hotels/{group_id}/reservations")
    suspend fun listReservations(
        @Path("group_id") groupId: String,
        @Query("guest_email") guestEmail: String
    ): Response<List<Any>> // Cambiar Any por el DTO correcto si la API devuelve datos específicos
}