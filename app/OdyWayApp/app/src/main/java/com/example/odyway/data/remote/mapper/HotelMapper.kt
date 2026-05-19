package com.example.odyway.data.remote.mapper

import com.example.odyway.data.remote.dto.HotelDto
import com.example.odyway.data.remote.dto.RoomDto
import com.example.odyway.domain.Hotel
import com.example.odyway.domain.Room

fun HotelDto.toDomain(): Hotel {
    return Hotel(
        id = this.id ?: "",
        name = this.name ?: "Hotel sin nombre",
        address = this.address ?: "Dirección desconocida",
        rating = this.rating ?: 0,
        rooms = this.rooms?.map { it.toDomain() } ?: emptyList(),
        imageUrl = this.imageUrl ?: ""
    )
}

fun RoomDto.toDomain(): Room {
    return Room(
        id = this.id ?: "",
        roomType = this.roomType ?: "Habitación estándar",
        price = this.price ?: 0.0,
        images = this.images ?: emptyList()
    )
}