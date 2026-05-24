package com.example.odyway.data.remote.dto

import com.google.gson.annotations.SerializedName

data class HotelDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("address") val address: String,
    @SerializedName("rating") val rating: Int,
    @SerializedName("rooms") val rooms: List<RoomDto>,
    @SerializedName("image_url") val imageUrl: String
)

data class RoomDto(
    @SerializedName("id") val id: String,
    @SerializedName("room_type") val roomType: String,
    @SerializedName("price") val price: Double,
    @SerializedName("images") val images: List<String>
)

data class ReserveRequestDto(
    @SerializedName("hotel_id") val hotelId: String,
    @SerializedName("room_id") val roomId: String,
    @SerializedName("start_date") val startDate: String, // formato "YYYY-MM-DD"
    @SerializedName("end_date") val endDate: String,
    @SerializedName("guest_name") val guestName: String,
    @SerializedName("guest_email") val guestEmail: String
)

data class AvailabilityResponseDto(
    @SerializedName("available_hotels") val availableHotels: List<HotelDto>? = null
)

data class ReservationDto(
    @SerializedName("id") val id: String,
    @SerializedName("hotel_id") val hotelId: String,
    @SerializedName("room_id") val roomId: String,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("end_date") val endDate: String,
    @SerializedName("guest_name") val guestName: String,
    @SerializedName("guest_email") val guestEmail: String,
    @SerializedName("hotel") val hotel: HotelDto?,
    @SerializedName("room") val room: RoomDto?
)

data class ReservationListDto(
    @SerializedName("reservations") val reservations: List<ReservationDto>
)