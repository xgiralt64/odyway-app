package com.example.odyway.data.fakeDB

import android.util.Log
import com.example.odyway.domain.ItineraryItem
import com.example.odyway.domain.Trip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.LocalTime

/**
 * Fuente de datos en memoria (Fake DB).
 * Los datos se perderán al cerrar la app, cumpliendo con el requisito del Sprint 02.
 */
object FakeTripDataSource {

    private const val TAG = "FakeTripDataSource"

    // Estados internos mutables
    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    private val _itinerary = MutableStateFlow<List<ItineraryItem>>(emptyList())

    // Estados públicos inmutables que observará la UI
    val trips: StateFlow<List<Trip>> = _trips.asStateFlow()
    val itinerary: StateFlow<List<ItineraryItem>> = _itinerary.asStateFlow()

    init {
        Log.d(TAG, "Inicializando base de datos en memoria (Fake Dataset)")
        loadFakeData()
    }

    /**
     * Carga unos datos iniciales para que la app no arranque vacía.
     */
    private fun loadFakeData() {
        val today = LocalDate.now()

        val fakeTrip = Trip(
            id = "trip-001",
            userId = "user-1",
            title = "Aventura en Barcelona",
            destination = "Barcelona, España",
            description = "Escapada de fin de semana para visitar la Sagrada Familia y comer tapas.",
            status = "Próximo",
            startDate = today.plusDays(5),
            endDate = today.plusDays(10),
            budget = 600.0
        )

        val fakeActivity = ItineraryItem(
            id = "act-001",
            tripId = "trip-001",
            date = today.plusDays(6),
            time = LocalTime.of(10, 30),
            title = "Visita Sagrada Familia",
            description = "Entrada general reservada online.",
            location = "Carrer de Mallorca, 401",
            isCompleted = false
        )

        _trips.value = listOf(fakeTrip)
        _itinerary.value = listOf(fakeActivity)
    }

    // ==========================================
    // CRUD DE VIAJES
    // ==========================================

    @Synchronized
    fun getTripById(id: String): Trip? {
        return _trips.value.find { it.id == id }
    }

    @Synchronized
    fun addTrip(trip: Trip): Boolean {
        if (_trips.value.any { it.id == trip.id }) return false
        _trips.value = _trips.value + trip
        return true
    }

    @Synchronized
    fun updateTrip(trip: Trip): Boolean {
        val index = _trips.value.indexOfFirst { it.id == trip.id }
        if (index == -1) return false

        val updatedList = _trips.value.toMutableList()
        updatedList[index] = trip
        _trips.value = updatedList
        return true
    }

    @Synchronized
    fun deleteTrip(id: String): Boolean {
        val initialSize = _trips.value.size
        _trips.value = _trips.value.filter { it.id != id }

        if (_trips.value.size < initialSize) {
            // Si borramos el viaje, borramos también sus actividades asociadas
            _itinerary.value = _itinerary.value.filter { it.tripId != id }
            return true
        }
        return false
    }

    // ==========================================
    // CRUD DE ITINERARIO
    // ==========================================

    @Synchronized
    fun addItineraryItem(item: ItineraryItem): Boolean {
        if (_itinerary.value.any { it.id == item.id }) return false
        _itinerary.value = _itinerary.value + item
        return true
    }

    @Synchronized
    fun updateItineraryItem(item: ItineraryItem): Boolean {
        val index = _itinerary.value.indexOfFirst { it.id == item.id }
        if (index == -1) return false

        val updatedList = _itinerary.value.toMutableList()
        updatedList[index] = item
        _itinerary.value = updatedList
        return true
    }

    @Synchronized
    fun deleteItineraryItem(itemId: String): Boolean {
        val initialSize = _itinerary.value.size
        _itinerary.value = _itinerary.value.filter { it.id != itemId }
        return _itinerary.value.size < initialSize
    }
}