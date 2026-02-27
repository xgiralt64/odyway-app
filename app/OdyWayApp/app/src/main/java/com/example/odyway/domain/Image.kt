package com.example.odyway.domain
data class Image(
    val id: String,
    val url: String,
    val description: String
) {

    fun isValidUrl(): Boolean {
        return url.startsWith("http")
    }
}