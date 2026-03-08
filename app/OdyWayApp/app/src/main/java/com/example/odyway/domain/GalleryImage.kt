package com.example.odyway.domain

data class GalleryImage(
    val id: String,
    val tripId: String,
    val imageUrl: String,
    val description: String?
) {
    fun isValidUrl(): Boolean {
        return imageUrl.startsWith("http")
    }
}