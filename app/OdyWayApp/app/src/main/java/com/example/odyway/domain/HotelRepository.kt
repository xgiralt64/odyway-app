package com.example.odyway.domain

interface HotelRepository {
    suspend fun checkAvailability(
        startDate: String,
        endDate: String,
        city: String? = null,
        hotelId: String? = null
    ): Result<List<Hotel>>

    suspend fun reserveRoom(
        hotelId: String,
        roomId: String,
        startDate: String,
        endDate: String,
        guestName: String,
        guestEmail: String,
        hotelName: String,
        roomType: String,
        price: Double
    ): Result<Unit>

    suspend fun getGroupReservations(guestEmail: String): Result<List<Reservation>>
    suspend fun cancelReservationById(resId: String, hotelId: String): Result<Unit>

}