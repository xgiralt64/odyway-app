package com.example.odyway.di

import android.content.Context
import android.content.SharedPreferences
import com.example.odyway.data.repository.TripRepositoryImpl
import com.example.odyway.domain.TripRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("odyway_settings", Context.MODE_PRIVATE)
    }

    // mas adelante lo cambiaremos para que use Room
    @Provides
    @Singleton
    fun provideTripRepository(): TripRepository {
        return TripRepositoryImpl()
    }
}