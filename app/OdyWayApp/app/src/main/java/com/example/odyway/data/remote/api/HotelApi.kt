package com.example.odyway.data.remote.api

import com.example.odyway.data.remote.dto.AvailabilityResponseDto
import com.example.odyway.data.remote.dto.ReserveRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface HotelApi {

    // ==========================================
    // FASES 1 y 2: BÚSQUEDA Y RESERVA
    // ==========================================

    // Buscar disponibilidad de hoteles
    @GET("/hotels/{group_id}/availability")
    suspend fun checkAvailability(
        @Path("group_id") groupId: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("city") city: String? = null,
        @Query("hotel_id") hotelId: String? = null
    ): Response<AvailabilityResponseDto>

    // Hacer una reserva
    @POST("/hotels/{group_id}/reserve")
    suspend fun reserveRoom(
        @Path("group_id") groupId: String,
        @Body request: ReserveRequestDto
    ): Response<Unit>

    //RESERVAS
    // Obtener las reservas del servidor filtrando por email de usuario
    @GET("/hotels/{group_id}/reservations")
    suspend fun getReservations(
        @Path("group_id") groupId: String,
        @Query("guest_email") guestEmail: String? = null
    ): Response<com.example.odyway.data.remote.dto.ReservationListDto>

    // Cancelar una reserva remota por su ID
    @DELETE("/reservations/{res_id}")
    suspend fun cancelReservationById(
        @Path("res_id") resId: String
    ): Response<Unit>
}