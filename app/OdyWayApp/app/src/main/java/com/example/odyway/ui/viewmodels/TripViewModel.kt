package com.example.odyway.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.odyway.domain.ItineraryItem
import com.example.odyway.domain.Trip
import com.example.odyway.domain.TripRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class TripViewModel(
    private val repository: TripRepository
) : ViewModel() {

    // Cambiado a minúscula ("tag") para evitar el warning amarillo de Android Studio
    private val tag = "TripViewModel_LOG"

    // ==========================================
    // 1. ESTADOS DE LA INTERFAZ (UI STATE)
    // ==========================================

    val trips: StateFlow<List<Trip>> = repository.getAllTrips()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _currentItinerary = MutableStateFlow<List<ItineraryItem>>(emptyList())
    val currentItinerary: StateFlow<List<ItineraryItem>> = _currentItinerary.asStateFlow()

    private val _uiErrorMessage = MutableStateFlow<String?>(null)
    val uiErrorMessage: StateFlow<String?> = _uiErrorMessage.asStateFlow()

    init {
        Log.d(tag, "Inicializando TripViewModel...")
    }

    // ==========================================
    // 2. OPERACIONES DE VIAJES (TRIPS)
    // ==========================================

    fun addTrip(trip: Trip) {
        if (!validateTripDates(trip.startDate, trip.endDate) || !validateRequiredTripFields(trip)) {
            return
        }

        viewModelScope.launch {
            Log.d(tag, "Intentando añadir nuevo viaje: ${trip.title}")
            repository.addTrip(trip)
                .onFailure { error ->
                    showError(error.message ?: "Error desconocido al crear el viaje")
                }
                .onSuccess {
                    Log.i(tag, "Viaje añadido con éxito: ${trip.id}")
                }
        }
    }

    fun updateTrip(trip: Trip) {
        if (!validateTripDates(trip.startDate, trip.endDate) || !validateRequiredTripFields(trip)) {
            return
        }

        viewModelScope.launch {
            Log.d(tag, "Intentando actualizar viaje: ${trip.id}")
            repository.updateTrip(trip)
                .onFailure { error ->
                    showError(error.message ?: "Error al actualizar el viaje")
                }
                .onSuccess {
                    Log.i(tag, "Viaje actualizado con éxito: ${trip.title}")
                }
        }
    }

    fun deleteTrip(tripId: String) {
        viewModelScope.launch {
            Log.d(tag, "Intentando borrar viaje con ID: $tripId")
            repository.deleteTrip(tripId)
                .onFailure { error ->
                    showError(error.message ?: "Error al borrar el viaje")
                }
                .onSuccess {
                    Log.i(tag, "Viaje y su itinerario borrados con éxito.")
                }
        }
    }

    // ==========================================
    // 3. OPERACIONES DE ITINERARIO (ACTIVIDADES)
    // ==========================================

    fun loadItineraryForTrip(tripId: String) {
        viewModelScope.launch {
            Log.d(tag, "Cargando itinerario para el viaje: $tripId")
            repository.getItineraryForTrip(tripId).collect { items ->
                _currentItinerary.value = items
                Log.i(tag, "Itinerario cargado. Total de actividades: ${items.size}")
            }
        }
    }

    fun addItineraryItem(trip: Trip, item: ItineraryItem) {
        if (!validateActivityDateWithinTrip(trip, item.date) || !validateRequiredActivityFields(item)) {
            return
        }

        viewModelScope.launch {
            Log.d(tag, "Añadiendo actividad '${item.title}' al viaje ${trip.id}")
            repository.addItineraryItem(item)
                .onFailure { error ->
                    showError(error.message ?: "Error al añadir la actividad")
                }
                .onSuccess {
                    Log.i(tag, "Actividad añadida con éxito.")
                }
        }
    }

    fun updateItineraryItem(trip: Trip, item: ItineraryItem) {
        if (!validateActivityDateWithinTrip(trip, item.date) || !validateRequiredActivityFields(item)) {
            return
        }

        viewModelScope.launch {
            Log.d(tag, "Actualizando actividad: ${item.id}")
            repository.updateItineraryItem(item)
                .onFailure { error ->
                    showError(error.message ?: "Error al actualizar la actividad")
                }
                .onSuccess {
                    Log.i(tag, "Actividad actualizada con éxito.")
                }
        }
    }

    fun deleteItineraryItem(itemId: String) {
        viewModelScope.launch {
            Log.d(tag, "Borrando actividad con ID: $itemId")
            repository.deleteItineraryItem(itemId)
                .onFailure { error ->
                    showError(error.message ?: "Error al borrar la actividad")
                }
                .onSuccess {
                    Log.i(tag, "Actividad borrada con éxito.")
                }
        }
    }

    // ==========================================
    // 4. LÓGICA DE VALIDACIÓN (REQUISITOS DEL PDF T3.1)
    // ==========================================

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

    // ==========================================
    // 5. GESTIÓN DE ERRORES UI Y LOGS
    // ==========================================

    private fun showError(message: String) {
        Log.e(tag, "Error de Validación/Operación: $message")
        _uiErrorMessage.value = message
    }

    fun clearErrorMessage() {
        _uiErrorMessage.value = null
    }
}