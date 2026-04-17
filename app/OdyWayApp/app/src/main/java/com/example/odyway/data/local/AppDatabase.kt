package com.example.odyway.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.odyway.data.local.dao.ItineraryDao
import com.example.odyway.data.local.dao.TripDao
import com.example.odyway.data.local.dao.UserAndLogDao
import com.example.odyway.data.local.entity.AccessLogEntity
import com.example.odyway.data.local.entity.ItineraryItemEntity
import com.example.odyway.data.local.entity.TripEntity
import com.example.odyway.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        TripEntity::class,
        ItineraryItemEntity::class,
        AccessLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class) // Le indicamos a Room cmmo manejar las fechas
abstract class AppDatabase : RoomDatabase() {

    abstract fun tripDao(): TripDao
    abstract fun itineraryDao(): ItineraryDao
    abstract fun userAndLogDao(): UserAndLogDao
}