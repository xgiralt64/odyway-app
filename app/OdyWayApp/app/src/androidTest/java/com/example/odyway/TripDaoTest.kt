package com.example.odyway.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.odyway.data.local.AppDatabase
import com.example.odyway.data.local.dao.TripDao
import com.example.odyway.data.local.dao.UserAndLogDao
import com.example.odyway.data.local.entity.TripEntity
import com.example.odyway.data.local.entity.UserEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TripDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var tripDao: TripDao
    private lateinit var userDao: UserAndLogDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        tripDao = database.tripDao()
        userDao = database.userAndLogDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    // Función de ayuda para crear un usuario antes de cada test
    private suspend fun insertDummyUser() {
        val testUser = UserEntity(
            id = "user_1",
            name = "Test User",
            username = "testuser",
            email = "test@test.com",
            profileImageUrl = null,
            birthDate = 0L,
            login = "test@test.com",
            address = "",
            country = "",
            phone = "",
            acceptEmails = false
        )
        userDao.insertUser(testUser)
    }

    @Test
    fun insertAndGetTrip() = runBlocking {
        //GIVEN (Dado un usuario existente y un viaje falso
        insertDummyUser()

        val testTrip = TripEntity(
            id = "trip_123",
            userId = "user_1",
            title = "Viaje a Roma",
            destination = "Roma",
            description = "Fin de semana en Italia",
            status = "PLANNED",
            startDate = java.time.LocalDate.of(2024, 5, 10),
            endDate = java.time.LocalDate.of(2024, 5, 15),
            budget = 500.0
        )

        // WHEN
        tripDao.insertTrip(testTrip)

        // THEN
        val retrievedTrip = tripDao.getTripById("trip_123")
        assertNotNull(retrievedTrip)
        assertEquals("Viaje a Roma", retrievedTrip?.title)
    }

    @Test
    fun deleteTrip() = runBlocking {
        //GIVEN
        insertDummyUser() //insertamos user primero

        val testTrip = TripEntity(
            id = "trip_999",
            userId = "user_1",
            title = "Viaje a Borrar",
            destination = "Test",
            description = "",
            status = "PLANNED",
            startDate = java.time.LocalDate.now(),
            endDate = java.time.LocalDate.now(),
            budget = 0.0
        )
        tripDao.insertTrip(testTrip)

        // WHEN
        tripDao.deleteTrip("trip_999")

        // THEN
        val retrievedTrip = tripDao.getTripById("trip_999")
        assertNull(retrievedTrip)
    }
}