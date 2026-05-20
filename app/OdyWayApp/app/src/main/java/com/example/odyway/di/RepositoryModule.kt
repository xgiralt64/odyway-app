package com.example.odyway.di

import com.example.odyway.data.repository.AuthRepositoryImpl
import com.example.odyway.data.repository.HotelRepositoryImpl
import com.example.odyway.data.repository.ItineraryRepositoryImpl
import com.example.odyway.data.repository.TripRepositoryImpl
import com.example.odyway.domain.AuthRepository
import com.example.odyway.domain.HotelRepository
import com.example.odyway.domain.ItineraryRepository
import com.example.odyway.domain.TripRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    // @Binds es una forma más óptima que @Provides para decirle a Hilt:
    // "Cuando alguien pida la interfaz TripRepository, dale TripRepositoryImpl"
    @Binds
    @Singleton
    abstract fun bindTripRepository(
        tripRepositoryImpl: TripRepositoryImpl
    ): TripRepository

    @Binds
    @Singleton
    abstract fun bindItineraryRepository(
        itineraryRepositoryImpl: ItineraryRepositoryImpl
    ): ItineraryRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    // ==========================================
    // AÑADIDO: Binding para HotelRepository
    // ==========================================
    @Binds
    @Singleton
    abstract fun bindHotelRepository(
        hotelRepositoryImpl: HotelRepositoryImpl
    ): HotelRepository
}