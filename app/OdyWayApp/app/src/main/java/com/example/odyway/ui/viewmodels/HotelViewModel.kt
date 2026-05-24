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

data class HotelSearchUiState(
    val isLoading: Boolean = false,
    val hotels: List<Hotel> = emptyList(),
    val errorMessage: String? = null,
    val searchStartDate: String = "",
    val searchEndDate: String = ""
)

@HiltViewModel
class HotelViewModel @Inject constructor(
    private val hotelRepository: HotelRepository
) : ViewModel() {

    private val tag = "HotelViewModel"

    private val _uiState = MutableStateFlow(HotelSearchUiState())
    val uiState: StateFlow<HotelSearchUiState> = _uiState.asStateFlow()

    // BÚSQUEDA DE HOTELES

    fun searchHotels(city: String, startDate: String, endDate: String) {
        if (city.isBlank() || startDate.isBlank() || endDate.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Por favor, introduce una ciudad y las fechas."
            )
            return
        }

        viewModelScope.launch {
            // Guardamos las fechas en el estado para que cualquier pantalla las pueda consultar
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                hotels = emptyList(),
                searchStartDate = startDate,
                searchEndDate = endDate
            )

            Log.d(tag, "Buscando hoteles en $city del $startDate al $endDate...")

            val result = hotelRepository.checkAvailability(
                startDate = startDate,
                endDate = endDate,
                city = city.trim().lowercase()
            )

            result.onSuccess { hotelList ->
                _uiState.value = _uiState.value.copy(isLoading = false, hotels = hotelList)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Error desconocido"
                )
            }
        }
    }

    // RESERVAR HABITACION

    fun reserveRoom(
        hotelId: String,
        roomId: String,
        guestName: String,
        guestEmail: String,
        hotelName: String,
        roomType: String,
        price: Double,
        onSuccess: () -> Unit
    ) {
        // Recuperamos las fechas directamente desde nuestro estado guardado
        val startDate = _uiState.value.searchStartDate
        val endDate = _uiState.value.searchEndDate

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            Log.d(tag, "Reservando habitación $roomId en hotel $hotelId del $startDate al $endDate...")

            val result = hotelRepository.reserveRoom(
                hotelId = hotelId,
                roomId = roomId,
                startDate = startDate,
                endDate = endDate,
                guestName = guestName,
                guestEmail = guestEmail,
                hotelName = hotelName,
                roomType = roomType,
                price = price
            )

            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false)
                onSuccess()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "No se pudo realizar la reserva."
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }


    //LOGICA RESERVAS
    private val _reservations = MutableStateFlow<List<com.example.odyway.domain.Reservation>>(emptyList())
    val reservations: StateFlow<List<com.example.odyway.domain.Reservation>> = _reservations.asStateFlow()

    fun loadReservations(email: String) {
        if (email.isBlank()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = hotelRepository.getGroupReservations(email)

            result.onSuccess { list ->
                _reservations.value = list
                _uiState.value = _uiState.value.copy(isLoading = false)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "No se pudieron cargar las reservas."
                )
            }
        }
    }

    fun cancelReservation(resId: String, hotelId: String, email: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            // Le pasamos tanto el ID de la reserva como el del hotel
            val result = hotelRepository.cancelReservationById(resId, hotelId)

            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false)
                onSuccess()
                loadReservations(email) // Recargamos la lista automáticamente tras borrar
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "No se pudo cancelar la reserva."
                )
            }
        }
    }
}