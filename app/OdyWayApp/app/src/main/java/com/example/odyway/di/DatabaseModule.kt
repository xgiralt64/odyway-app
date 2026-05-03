package com.example.odyway.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.odyway.data.local.AppDatabase
import com.example.odyway.data.local.dao.ItineraryDao
import com.example.odyway.data.local.dao.TripDao
import com.example.odyway.data.local.dao.UserAndLogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("odyway_settings", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "odyway_db"
        ).fallbackToDestructiveMigration().build()
    }


    @Provides
    fun provideTripDao(database: AppDatabase): TripDao = database.tripDao()

    @Provides
    fun provideItineraryDao(database: AppDatabase): ItineraryDao = database.itineraryDao()

    @Provides
    fun provideUserAndLogDao(database: AppDatabase): UserAndLogDao = database.userAndLogDao()
}