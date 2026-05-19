package com.example.odyway.domain

interface HotelRepository {
    suspend fun checkAvailability(startDate: String, endDate: String, city: String? = null, hotelId: String? = null): Result<List<Hotel>>
    suspend fun reserveRoom(hotelId: String, roomId: String, startDate: String, endDate: String, guestName: String, guestEmail: String): Result<Unit>
}