package com.example.odyway

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.odyway.domain.ItineraryItem
import com.example.odyway.domain.Trip
import com.example.odyway.domain.TripRepository
import com.example.odyway.ui.viewmodels.TripViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class TripViewModelTest {

    // Regla necesaria para que los LiveData/StateFlow funcionen sincronamente en los tests
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: TripRepository
    private lateinit var viewModel: TripViewModel

    // Dispatcher para pruebas con Corrutinas
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        // Configuramos las corrutinas para los tests
        Dispatchers.setMain(testDispatcher)

        // 1. MOCKEAMOS LA CLASE LOG (Para cumplir el T3.5 sin que crashee Java)
        mockkStatic(Log::class)
        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.i(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>()) } returns 0

        // 2. MOCKEAMOS EL REPOSITORIO
        repository = mockk(relaxed = true)

        // Comportamiento simulado (Fake) del repositorio
        every { repository.getAllTrips() } returns MutableStateFlow(emptyList())

        // 3. INICIALIZAMOS EL VIEWMODEL
        viewModel = TripViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    // ==============================================================
    // TESTS DE VIAJES (TRIPS) - T3.1 y T3.2
    // ==============================================================

    @Test
    fun `addTrip con datos validos llama al repositorio`() {
        val validTrip = Trip(
            id = "trip-1", userId = "u1", title = "París", destination = "Francia",
            description = "", status = "Próxim", startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(5), budget = 1000.0
        )

        // Simulamos que el repositorio devuelve un éxito
        coEvery { repository.addTrip(any()) } returns Result.success(Unit)

        viewModel.addTrip(validTrip)

        // Verificamos que se llamó al repositorio para guardarlo
        coVerify { repository.addTrip(validTrip) }

        // Verificamos que no hay errores en la UI
        assertEquals(null, viewModel.uiErrorMessage.value)
    }

    @Test
    fun `addTrip con titulo vacio muestra error en UI y rechaza guardado`() {
        val invalidTrip = Trip(
            id = "trip-2", userId = "u1", title = "", destination = "Francia", // <-- Título vacío
            description = "", status = "Próxim", startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(5), budget = 1000.0
        )

        viewModel.addTrip(invalidTrip)

        // Verificamos que NO se llamó al repositorio
        coVerify(exactly = 0) { repository.addTrip(any()) }

        // Verificamos que el StateFlow de error se ha llenado con el mensaje correcto
        assertEquals("El título y el destino son obligatorios.", viewModel.uiErrorMessage.value)
    }

    @Test
    fun `addTrip con fechas invertidas muestra error en UI y rechaza guardado`() {
        val invalidTrip = Trip(
            id = "trip-3", userId = "u1", title = "Roma", destination = "Italia",
            description = "", status = "Próxim",
            startDate = LocalDate.now().plusDays(5),
            endDate = LocalDate.now() // <-- Termina antes de empezar
            , budget = 1000.0
        )

        viewModel.addTrip(invalidTrip)

        coVerify(exactly = 0) { repository.addTrip(any()) }
        assertEquals("La fecha de inicio no puede ser posterior a la fecha de fin.", viewModel.uiErrorMessage.value)
    }

    // ==============================================================
    // TESTS DE ITINERARIO (ACTIVITIES) - T3.1 y T3.2
    // ==============================================================

    @Test
    fun `addItineraryItem dentro del rango del viaje es valido`() {
        val trip = Trip(
            id = "trip-1", userId = "u1", title = "París", destination = "Francia",
            description = "", status = "Próxim", startDate = LocalDate.of(2025, 6, 1),
            endDate = LocalDate.of(2025, 6, 10), budget = 1000.0
        )
        val validItem = ItineraryItem(
            id = "act-1", tripId = "trip-1", title = "Torre Eiffel", description = "Visita",
            location = "París", category = "Visita", date = LocalDate.of(2025, 6, 5), time = LocalTime.NOON
        )

        coEvery { repository.addItineraryItem(any()) } returns Result.success(Unit)

        viewModel.addItineraryItem(trip, validItem)

        coVerify { repository.addItineraryItem(validItem) }
        assertEquals(null, viewModel.uiErrorMessage.value)
    }

    @Test
    fun `addItineraryItem FUERA del rango del viaje muestra error`() {
        val trip = Trip(
            id = "trip-1", userId = "u1", title = "París", destination = "Francia",
            description = "", status = "Próxim", startDate = LocalDate.of(2025, 6, 1),
            endDate = LocalDate.of(2025, 6, 10), budget = 1000.0
        )
        val invalidItem = ItineraryItem(
            id = "act-2", tripId = "trip-1", title = "Louvre", description = "Visita",
            location = "París", category = "Visita",
            date = LocalDate.of(2025, 6, 15), // <-- Fuera del rango (El viaje acaba el día 10)
            time = LocalTime.NOON
        )

        viewModel.addItineraryItem(trip, invalidItem)

        coVerify(exactly = 0) { repository.addItineraryItem(any()) }
        assertEquals("La actividad debe estar entre el 2025-06-01 y el 2025-06-10.", viewModel.uiErrorMessage.value)
    }

    @Test
    fun `deleteTrip y deleteItineraryItem llaman al repositorio correctamente`() {
        coEvery { repository.deleteTrip(any()) } returns Result.success(Unit)
        coEvery { repository.deleteItineraryItem(any()) } returns Result.success(Unit)

        viewModel.deleteTrip("trip-1")
        viewModel.deleteItineraryItem("act-1")

        coVerify { repository.deleteTrip("trip-1") }
        coVerify { repository.deleteItineraryItem("act-1") }
    }
}