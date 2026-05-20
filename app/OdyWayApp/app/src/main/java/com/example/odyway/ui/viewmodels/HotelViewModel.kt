package com.example.odyway.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.odyway.domain.Hotel
import com.example.odyway.domain.HotelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// 1. Definimos el Estado de la Interfaz (Súper útil para Compose)
data class HotelSearchUiState(
    val isLoading: Boolean = false,
    val hotels: List<Hotel> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class HotelViewModel @Inject constructor(
    private val hotelRepository: HotelRepository
) : ViewModel() {

    private val tag = "HotelViewModel"

    // 2. Tubería reactiva del estado
    private val _uiState = MutableStateFlow(HotelSearchUiState())
    val uiState: StateFlow<HotelSearchUiState> = _uiState.asStateFlow()

    // ==========================================
    // BÚSQUEDA DE HOTELES (T2.1 y T2.2)
    // ==========================================
    fun searchHotels(city: String, startDate: String, endDate: String) {
        // Validación básica
        if (city.isBlank() || startDate.isBlank() || endDate.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Por favor, introduce una ciudad y las fechas."
            )
            return
        }

        viewModelScope.launch {
            // Activamos el circulito de carga y limpiamos errores y hoteles anteriores
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                hotels = emptyList()
            )

            Log.d(tag, "Buscando hoteles en $city del $startDate al $endDate...")

            val result = hotelRepository.checkAvailability(
                startDate = startDate,
                endDate = endDate,
                city = city.trim().lowercase() // La API suele preferir minúsculas
            )

            result.onSuccess { hotelList ->
                Log.i(tag, "Se encontraron ${hotelList.size} hoteles.")
                // Desactivamos la carga y guardamos la lista
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    hotels = hotelList
                )
            }.onFailure { error ->
                Log.e(tag, "Error en la búsqueda: ${error.message}")
                // Mostramos el error
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Error desconocido"
                )
            }
        }
    }

    // ==========================================
    // RESERVAR HABITACIÓN (T2.3)
    // ==========================================
    fun reserveRoom(
        hotelId: String,
        roomId: String,
        startDate: String,
        endDate: String,
        guestName: String,
        guestEmail: String,
        onSuccess: () -> Unit // Función que llamaremos para avisar a la pantalla de que vuelva atrás
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            Log.d(tag, "Reservando habitación $roomId en hotel $hotelId...")

            val result = hotelRepository.reserveRoom(
                hotelId, roomId, startDate, endDate, guestName, guestEmail
            )

            result.onSuccess {
                Log.i(tag, "Reserva exitosa guardada en API y Room.")
                _uiState.value = _uiState.value.copy(isLoading = false)
                onSuccess() // Avisamos a Compose para hacer un nav.popBackStack() o mostrar Toast
            }.onFailure { error ->
                Log.e(tag, "Error al reservar: ${error.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "No se pudo realizar la reserva."
                )
            }
        }
    }

    // ==========================================
    // UTILIDADES
    // ==========================================
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}