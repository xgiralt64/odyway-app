package com.example.odyway.di

import android.R.attr.level
import com.example.odyway.BuildConfig
import com.example.odyway.data.remote.api.HotelApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // URL base de la API del profesor (¡Recuerda los permisos de ClearTextTraffic en el Manifest!)
    private const val BASE_URL = BuildConfig.HOTELS_API_URL

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        // Esto nos permitirá ver las peticiones y respuestas en el Logcat (filtra por "OkHttp")
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideHotelApi(retrofit: Retrofit): HotelApi {
        return retrofit.create(HotelApi::class.java)
    }
}