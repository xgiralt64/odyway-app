package com.example.odyway.data.local.dao

import androidx.room.*
import com.example.odyway.data.local.entity.ItineraryItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItineraryDao {
    // Obtenemos el itinerario ordenado por fecha y hora
    @Query("SELECT * FROM itinerary_items WHERE tripId = :tripId ORDER BY date ASC, time ASC")
    fun getItineraryForTrip(tripId: String): Flow<List<ItineraryItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItineraryItem(item: ItineraryItemEntity)

    @Update
    suspend fun updateItineraryItem(item: ItineraryItemEntity)

    @Query("DELETE FROM itinerary_items WHERE id = :itemId")
    suspend fun deleteItineraryItem(itemId: String)
}