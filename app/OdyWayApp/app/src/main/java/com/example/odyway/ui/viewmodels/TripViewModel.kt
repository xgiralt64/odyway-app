package com.example.odyway.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.odyway.domain.Activity
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

    private val TAG = "TripViewModel"

    // ==========================================
    // 1. ESTADOS DE LA INTERFAZ (UI STATE)
    // ==========================================

    // Lista de viajes (Sobrevive a la rotación gracias a stateIn)
    val trips: StateFlow<List<Trip>> = repository.getAllTrips()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Lista del itinerario del viaje actualmente seleccionado
    private val _currentItinerary = MutableStateFlow<List<ItineraryItem>>(emptyList())
    val currentItinerary: StateFlow<List<ItineraryItem>> = _currentItinerary.asStateFlow()

    // Estado para mostrar errores en la pantalla (Ej: "La fecha es inválida")
    private val _uiErrorMessage = MutableStateFlow<String?>(null)
    val uiErrorMessage: StateFlow<String?> = _uiErrorMessage.asStateFlow()

    // ==========================================
    // 2. OPERACIONES DE VIAJES (TRIPS)
    // ==========================================

    fun addTrip(trip: Trip) {
        if (!validateTripDates(trip.startDate, trip.endDate) || !validateRequiredTripFields(trip)) {
            return // Si la validación falla, detenemos la operación
        }

        viewModelScope.launch {
            repository.addTrip(trip).onFailure { error ->
                showError(error.message ?: "Error desconocido al crear el viaje")
            }
        }
    }

    fun updateTrip(trip: Trip) {
        if (!validateTripDates(trip.startDate, trip.endDate) || !validateRequiredTripFields(trip)) {
            return
        }

        viewModelScope.launch {
            repository.updateTrip(trip).onFailure { error ->
                showError(error.message ?: "Error al actualizar el viaje")
            }
        }
    }

    fun deleteTrip(tripId: String) {
        viewModelScope.launch {
            repository.deleteTrip(tripId).onFailure { error ->
                showError(error.message ?: "Error al borrar el viaje")
            }
        }
    }

    // ==========================================
    // 3. OPERACIONES DE ITINERARIO (ACTIVIDADES)
    // ==========================================

    /**
     * Carga el itinerario de un viaje específico para mostrarlo en la pantalla de detalle.
     */
    fun loadItineraryForTrip(tripId: String) {
        viewModelScope.launch {
            repository.getItineraryForTrip(tripId).collect { items ->
                _currentItinerary.value = items
            }
        }
    }

    fun addItineraryItem(trip: Trip, item: ItineraryItem) {
        if (!validateActivityDateWithinTrip(trip, item.date) || !validateRequiredActivityFields(item)) {
            return
        }

        viewModelScope.launch {
            repository.addItineraryItem(item).onFailure { error ->
                showError(error.message ?: "Error al añadir la actividad")
            }
        }
    }

    fun deleteItineraryItem(itemId: String) {
        viewModelScope.launch {
            repository.deleteItineraryItem(itemId).onFailure { error ->
                showError(error.message ?: "Error al borrar la actividad")
            }
        }
    }

    fun updateItineraryItem(trip: Trip, item: ItineraryItem) {
        // Validamos que los campos obligatorios y las fechas sean correctos
        if (!validateActivityDateWithinTrip(trip, item.date) || !validateRequiredActivityFields(item)) {
            return
        }

        viewModelScope.launch {
            repository.updateItineraryItem(item).onFailure { error ->
                showError(error.message ?: "Error al actualizar la actividad")
            }
        }
    }

    // ==========================================
    // 4. LÓGICA DE VALIDACIÓN (REQUISITOS DEL PDF)
    // ==========================================

    private fun validateRequiredTripFields(trip: Trip): Boolean {
        if (trip.title.isBlank() || trip.description.isBlank() || trip.destination.isBlank()) {
            showError("El título, destino y descripción son obligatorios.")
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
        if (item.title.isBlank() || item.description.isBlank()) {
            showError("El título y la descripción de la actividad son obligatorios.")
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
    // 5. GESTIÓN DE ERRORES UI
    // ==========================================

    private fun showError(message: String) {
        Log.e(TAG, "Validation/UI Error: $message")
        _uiErrorMessage.value = message
    }

    /**
     * La UI debe llamar a esta función después de mostrar el error (ej. al cerrar un Snackbar)
     */
    fun clearErrorMessage() {
        _uiErrorMessage.value = null
    }
}