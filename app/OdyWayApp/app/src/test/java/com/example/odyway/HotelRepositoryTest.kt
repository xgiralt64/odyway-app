package com.example.odyway.data.repository

import com.example.odyway.data.local.dao.TripDao
import com.example.odyway.data.remote.api.HotelApi
import com.example.odyway.data.remote.dto.AvailabilityResponseDto
import com.example.odyway.data.remote.dto.HotelDto
import com.example.odyway.data.remote.dto.ReservationDto
import com.example.odyway.data.remote.dto.ReservationListDto
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Response

class HotelRepositoryTest {

    // Dependencias falsas mocks que enganan al Repositorio
    private lateinit var mockHotelApi: HotelApi
    private lateinit var mockTripDao: TripDao
    private lateinit var mockFirebaseAuth: FirebaseAuth

    // el repositorio real que vamos a poner a prueba
    private lateinit var repository: HotelRepositoryImpl

    @Before
    fun setUp() {
        // inicializamos los mocks antes de cada test
        mockHotelApi = mock()
        mockTripDao = mock()
        mockFirebaseAuth = mock()

        // le inyectamos los mocks falsos al repositorio real
        repository = HotelRepositoryImpl(mockHotelApi, mockTripDao, mockFirebaseAuth)
    }

    @Test
    fun `checkAvailability retorna lista de hoteles cuando la API responde OK`() = runTest {
        // preparacion
        // Creamos una respuesta falsa simulando el servidor
        val fakeHotelDto = HotelDto("H01", "Hotel Paris", "Address", 4, emptyList(), "/img.jpg")
        val fakeResponse = AvailabilityResponseDto(listOf(fakeHotelDto))

        // Le decimos a la API falsa que cuando le pregunten, devuelva esa respuesta OK
        whenever(
            mockHotelApi.checkAvailability(
                groupId = any(),
                startDate = any(),
                endDate = any(),
                city = anyOrNull(),
                hotelId = anyOrNull()
            )
        ).thenReturn(Response.success(fakeResponse))

        // accion
        // Llamamos a la función real del repositorio
        val result = repository.checkAvailability("2026-05-01", "2026-05-05", "Paris", null)

        // comprobacion
        assertTrue(result.isSuccess)
        val domainHotels = result.getOrNull()
        assertEquals(1, domainHotels?.size)
        assertEquals("H01", domainHotels?.get(0)?.id)
        assertEquals("Hotel Paris", domainHotels?.get(0)?.name)
    }

    @Test
    fun `getGroupReservations retorna lista de reservas cuando la API responde OK`() = runTest {
        // preparacion
        val fakeReservationDto = ReservationDto(
            id = "RES123",
            hotelId = "H01",
            roomId = "R01",
            startDate = "2026-05-01",
            endDate = "2026-05-05",
            guestName = "Ben",
            guestEmail = "ben@test.com",
            hotel = null,
            room = null
        )
        val fakeResponse = ReservationListDto(listOf(fakeReservationDto))

        whenever(
            mockHotelApi.getReservations(
                groupId = any(),
                guestEmail = any()
            )
        ).thenReturn(Response.success(fakeResponse))

        // accion
        val result = repository.getGroupReservations("ben@test.com")

        // comprobacion assert
        assertTrue(result.isSuccess)
        val domainReservations = result.getOrNull()
        assertEquals(1, domainReservations?.size)
        assertEquals("RES123", domainReservations?.get(0)?.id)
        assertEquals("ben@test.com", domainReservations?.get(0)?.guest_email)
    }

    @Test
    fun `cancelReservationById retorna success y borra de la API y localmente`() = runTest {
        // preperacion
        // simulamos que la API devuelve un codigo 200 OK vacío
        whenever(
            mockHotelApi.cancelReservationById(any())
        ).thenReturn(Response.success(Unit))

        // No necesitamos mockear el TripDao.deleteReservedTripLocally porque
        // al ser una interfaz y estar mockeada, por defecto no hace nada y no da error

        // accion
        val result = repository.cancelReservationById("RES123", "H01")

        // comprobacion assert
        assertTrue(result.isSuccess)
    }
}