package com.example.odyway.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.odyway.domain.ItineraryItem
import com.example.odyway.domain.ItineraryRepository
import com.example.odyway.domain.Trip
import com.example.odyway.domain.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TripViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val itineraryRepository: ItineraryRepository
) : ViewModel() {

    private val tag = "TripViewModel_LOG"

    // ESTADOS DE LA INTERFAZ (UI STATE)

    val trips: StateFlow<List<Trip>> = tripRepository.getAllTrips()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // guardamos el id del viaje que el usuario esta mirando ahora mismo
    private val _currentTripId = MutableStateFlow<String?>(null)

    // escuchamos los cambios de ese id.  si cambia o se borra, pedimos los datos nuevos a Room al instante
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentItinerary: StateFlow<List<ItineraryItem>> = _currentTripId
        .flatMapLatest { tripId ->
            if (tripId != null) {
                Log.d(tag, "Conectando flujo de itinerario para: $tripId")
                itineraryRepository.getItineraryForTrip(tripId)
            } else {
                Log.d(tag, "No hay viaje seleccionado. Limpiando itinerario.")
                flowOf(emptyList()) // Si no hay viaje, lista vacía inmediata
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiErrorMessage = MutableStateFlow<String?>(null)
    val uiErrorMessage: StateFlow<String?> = _uiErrorMessage.asStateFlow()

    init {
        Log.d(tag, "Inicializando TripViewModel...")
    }

    // PERACIONES DE VIAJES (TRIPS)

    fun addTrip(trip: Trip) {
        if (!validateTripDates(trip.startDate, trip.endDate) || !validateRequiredTripFields(trip)) {
            return
        }

        viewModelScope.launch {
            Log.d(tag, "Intentando añadir nuevo viaje: ${trip.title}")
            tripRepository.addTrip(trip)
                .onFailure { error -> showError(error.message ?: "Error desconocido al crear el viaje") }
                .onSuccess { Log.i(tag, "Viaje añadido con éxito: ${trip.id}") }
        }
    }

    fun updateTrip(trip: Trip) {
        if (!validateTripDates(trip.startDate, trip.endDate) || !validateRequiredTripFields(trip)) {
            return
        }

        viewModelScope.launch {
            Log.d(tag, "Intentando actualizar viaje: ${trip.id}")
            tripRepository.updateTrip(trip)
                .onFailure { error -> showError(error.message ?: "Error al actualizar el viaje") }
                .onSuccess { Log.i(tag, "Viaje actualizado con éxito: ${trip.title}") }
        }
    }

    fun deleteTrip(tripId: String) {
        viewModelScope.launch {
            Log.d(tag, "Intentando borrar viaje con ID: $tripId")
            tripRepository.deleteTrip(tripId)
                .onFailure { error -> showError(error.message ?: "Error al borrar el viaje") }
                .onSuccess {
                    Log.i(tag, "Viaje y su itinerario borrados con éxito.")
                    // Si el viaje borrado es el que estabamos mirando, limpiamos la pantalla
                    if (_currentTripId.value == tripId) {
                        clearCurrentItinerary()
                    }
                }
        }
    }


    // OPERACIONES DE ITINERARIO (ACTIVIDADES)


    fun loadItineraryForTrip(tripId: String) {
        _currentTripId.value = tripId
    }

    // Funcion extra para limpiar la pantalla al salir de Detalles o al cerrar sesión
    fun clearCurrentItinerary() {
        _currentTripId.value = null
    }

    fun addItineraryItem(trip: Trip, item: ItineraryItem) {
        if (!validateActivityDateWithinTrip(trip, item.date) || !validateRequiredActivityFields(item)) {
            return
        }

        viewModelScope.launch {
            Log.d(tag, "Añadiendo actividad '${item.title}' al viaje ${trip.id}")
            itineraryRepository.addItineraryItem(item)
                .onFailure { error -> showError(error.message ?: "Error al añadir la actividad") }
                .onSuccess { Log.i(tag, "Actividad añadida con éxito.") }
        }
    }

    fun updateItineraryItem(trip: Trip, item: ItineraryItem) {
        if (!validateActivityDateWithinTrip(trip, item.date) || !validateRequiredActivityFields(item)) {
            return
        }

        viewModelScope.launch {
            Log.d(tag, "Actualizando actividad: ${item.id}")
            itineraryRepository.updateItineraryItem(item)
                .onFailure { error -> showError(error.message ?: "Error al actualizar la actividad") }
                .onSuccess { Log.i(tag, "Actividad actualizada con éxito.") }
        }
    }

    fun deleteItineraryItem(itemId: String) {
        viewModelScope.launch {
            Log.d(tag, "Borrando actividad con ID: $itemId")
            itineraryRepository.deleteItineraryItem(itemId)
                .onFailure { error -> showError(error.message ?: "Error al borrar la actividad") }
                .onSuccess { Log.i(tag, "Actividad borrada con éxito.") }
        }
    }


    // LOGICA DE VALIDACIÓN

    private fun validateRequiredTripFields(trip: Trip): Boolean {
        if (trip.title.isBlank() || trip.destination.isBlank()) {
            showError("El título y el destino son obligatorios.")
            return false
        }
        return true
    }

    private fun validateTripDates(startDate: LocalDate, endDate: LocalDate): Boolean {
        if (startDate.isAfter(endDate)) {
            showError("La fecha de inicio no puede ser posterior a la fecha de fin.")
            return false
        }
        return true
    }

    private fun validateRequiredActivityFields(item: ItineraryItem): Boolean {
        if (item.title.isBlank()) {
            showError("El título de la actividad es obligatorio.")
            return false
        }
        return true
    }

    private fun validateActivityDateWithinTrip(trip: Trip, activityDate: LocalDate): Boolean {
        if (activityDate.isBefore(trip.startDate) || activityDate.isAfter(trip.endDate)) {
            showError("La actividad debe estar entre el ${trip.startDate} y el ${trip.endDate}.")
            return false
        }
        return true
    }

    // GESTIÓN DE ERRORES UI Y LOGS

    private fun showError(message: String) {
        Log.e(tag, "Error de Validación/Operación: $message")
        _uiErrorMessage.value = message
    }

    fun clearErrorMessage() {
        _uiErrorMessage.value = null
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val tripImages: StateFlow<List<com.example.odyway.domain.TripImage>> = _currentTripId
        .flatMapLatest { tripId ->
            if (tripId != null) {
                tripRepository.getTripImages(tripId)
            } else {
                flowOf(emptyList()) // Si no hay viaje, vaciamos la galería
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    fun saveImagesToInternalStorage(context: android.content.Context, tripId: String, uris: List<android.net.Uri>) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            uris.forEach { uri ->
                try {
                    // Generamos un nombre de archivo único para que no se sobreescriban
                    val fileName = "trip_${tripId}_${System.currentTimeMillis()}_${java.util.UUID.randomUUID().toString().take(5)}.jpg"

                    // Apuntamos a la carpeta privada y segura de nuestra app
                    val file = java.io.File(context.filesDir, fileName)

                    // Copiamos los bytes de la foto original a nuestro nuevo archivo
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        file.outputStream().use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }

                    // Guardamos la ruta absoluta y definitiva en Room
                    tripRepository.saveTripImage(tripId, file.absolutePath)
                } catch (e: Exception) {
                    android.util.Log.e("TripViewModel", "Error al copiar la imagen: ${e.message}")
                }
            }
        }
    }

    fun deleteTripImage(image: com.example.odyway.domain.TripImage) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                // Borramos el archivo físico del almacenamiento del móvil para liberar espacio
                val file = java.io.File(image.imagePath)
                if (file.exists()) {
                    file.delete()
                }
                // Lo borramos de la base de datos de Room
                tripRepository.deleteTripImage(image.id)
            } catch (e: Exception) {
                android.util.Log.e("TripViewModel", "Error al borrar imagen: ${e.message}")
            }
        }
    }
}