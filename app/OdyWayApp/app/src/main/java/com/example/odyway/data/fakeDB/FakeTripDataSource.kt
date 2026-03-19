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

        // 1. VIAJE PRÓXIMO (Empieza en 5 días)
        val fakeTripUpcoming = Trip(
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

        // 2. VIAJE EN CURSO (Empezó hace 2 días y termina en 3 días)
        val fakeTripActive = Trip(
            id = "trip-002",
            userId = "user-1",
            title = "Ruta por Roma",
            destination = "Roma, Italia",
            description = "Comiendo pasta y viendo el Coliseo.",
            status = "En curso",
            startDate = today.minusDays(2),  // <-- ¡Empezó en el pasado!
            endDate = today.plusDays(3),     // <-- ¡Termina en el futuro!
            budget = 800.0
        )

        val fakeActivity_1 = ItineraryItem(
            id = "act-001",
            tripId = "trip-001",
            date = today.plusDays(10),
            time = LocalTime.of(10, 30),
            title = "Visita Sagrada Familia",
            description = "Entrada general reservada online. No olvidar cámara.",
            location = "Carrer de Mallorca, 401",
            isCompleted = false
        )

        val fakeActivity_2 = ItineraryItem(
            id = "act-002",
            tripId = "trip-001",
            date = today.plusDays(10),
            time = LocalTime.of(14, 0),
            title = "Comida en el Mercado de la Boqueria",
            description = "Tapas y productos frescos. Probar jamón ibérico.",
            location = "La Rambla, 91",
            isCompleted = false
        )

        val fakeActivity_3 = ItineraryItem(
            id = "act-003",
            tripId = "trip-001",
            date = today.plusDays(10),
            time = LocalTime.of(16, 30),
            title = "Paseo por el Barrio Gótico",
            description = "Recorrer calles medievales y visitar la Catedral.",
            location = "Barri Gòtic",
            isCompleted = false
        )

        val fakeActivity_4 = ItineraryItem(
            id = "act-004",
            tripId = "trip-001",
            date = today.plusDays(11),
            time = LocalTime.of(9, 0),
            title = "Park Güell",
            description = "Entrada a las 9h. Llegar 15 min antes. Ver zona monumental.",
            location = "Carrer d'Olot, s/n",
            isCompleted = false
        )

        val fakeActivity_5 = ItineraryItem(
            id = "act-005",
            tripId = "trip-001",
            date = today.plusDays(11),
            time = LocalTime.of(12, 30),
            title = "Casa Batlló",
            description = "Audio guía incluida. Tour de 1 hora aprox.",
            location = "Passeig de Gràcia, 43",
            isCompleted = false
        )

        val fakeActivity_6 = ItineraryItem(
            id = "act-006",
            tripId = "trip-001",
            date = today.plusDays(11),
            time = LocalTime.of(20, 0),
            title = "Cena en Barceloneta",
            description = "Restaurante frente al mar. Reserva confirmada para 4 personas.",
            location = "Passeig Marítim de la Barceloneta",
            isCompleted = false
        )

        val fakeActivity_7 = ItineraryItem(
            id = "act-007",
            tripId = "trip-001",
            date = today.plusDays(12),
            time = LocalTime.of(11, 0),
            title = "Montjuïc y Pueblo Español",
            description = "Teleférico + visita al castillo. Llevar calzado cómodo.",
            location = "Montjuïc",
            isCompleted = false
        )

        _trips.value = listOf(fakeTripUpcoming,fakeTripActive)
        _itinerary.value = listOf(
            fakeActivity_1,
            fakeActivity_2,
            fakeActivity_3,
            fakeActivity_4,
            fakeActivity_5,
            fakeActivity_6,
            fakeActivity_7
        )
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