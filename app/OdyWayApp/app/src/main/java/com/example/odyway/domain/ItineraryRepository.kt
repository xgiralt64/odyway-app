package com.example.odyway.domain

import kotlinx.coroutines.flow.Flow

interface ItineraryRepository {
    fun getItineraryForTrip(tripId: String): Flow<List<ItineraryItem>>
    suspend fun addItineraryItem(item: ItineraryItem): Result<Unit>
    suspend fun updateItineraryItem(item: ItineraryItem): Result<Unit>
    suspend fun deleteItineraryItem(itemId: String): Result<Unit>
}