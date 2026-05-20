package com.example.odyway.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.odyway.domain.Hotel
import com.example.odyway.domain.Room
import com.example.odyway.ui.viewmodels.HotelViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchHotelScreen(
    hotelViewModel: HotelViewModel,
    onNavigateBack: () -> Unit,
    userEmail: String = "guest@odyway.com", // Idealmente pasarlo desde AuthViewModel
    userName: String = "OdyWay Guest"
) {
    val uiState by hotelViewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Estados de búsqueda
    var city by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }

    // Estado para el diálogo de reserva
    var selectedRoom by remember { mutableStateOf<Pair<String, String>?>(null) } // Pair(hotelId, roomId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscar Hoteles") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            // PANEL DE BÚSQUEDA
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("Ciutat (ex: Barcelona, Paris, London)") },
                        leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DatePickerField(
                            label = "Data Inici",
                            selectedDate = startDate,
                            onDateSelected = { startDate = it },
                            modifier = Modifier.weight(1f)
                        )
                        DatePickerField(
                            label = "Data Fi",
                            selectedDate = endDate,
                            onDateSelected = { endDate = it },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val startStr = startDate?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: ""
                            val endStr = endDate?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: ""
                            hotelViewModel.searchHotels(city, startStr, endStr)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Buscar Disponibilitat", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // GESTIÓN DE ERRORES Y CARGA
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (uiState.errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                // LLISTA D'HOTELS
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(uiState.hotels) { hotel ->
                        HotelCard(
                            hotel = hotel,
                            onBookClick = { roomId ->
                                selectedRoom = Pair(hotel.id, roomId)
                            }
                        )
                    }
                }
            }
        }
    }

    // DIÀLEG DE CONFIRMACIÓ DE RESERVA (T2.3)
    if (selectedRoom != null) {
        AlertDialog(
            onDismissRequest = { selectedRoom = null },
            title = { Text("Confirmar Reserva") },
            text = { Text("Vols confirmar la reserva d'aquesta habitació? Es guardarà al teu planificador de viatges.") },
            confirmButton = {
                Button(
                    onClick = {
                        val startStr = startDate?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: ""
                        val endStr = endDate?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: ""

                        hotelViewModel.reserveRoom(
                            hotelId = selectedRoom!!.first,
                            roomId = selectedRoom!!.second,
                            startDate = startStr,
                            endDate = endStr,
                            guestName = userName,
                            guestEmail = userEmail,
                            onSuccess = {
                                Toast.makeText(context, "Reserva completada amb èxit!", Toast.LENGTH_LONG).show()
                                selectedRoom = null
                                onNavigateBack() // Tornem a la pantalla principal
                            }
                        )
                    }
                ) {
                    Text("Reservar")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedRoom = null }) { Text("Cancel·lar") }
            }
        )
    }
}

// ---------------- COMPONENTS REUTILITZABLES ----------------

@Composable
fun HotelCard(hotel: Hotel, onBookClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column {
            // Imatge de l'Hotel (Coil)
            AsyncImage(
                model = hotel.imageUrl,
                contentDescription = "Imatge de ${hotel.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = hotel.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row {
                        repeat(hotel.rating) {
                            Icon(Icons.Default.Hotel, contentDescription = "Estrella", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                Text(text = hotel.address, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))

                Spacer(modifier = Modifier.height(16.dp))
                Text("Habitacions disponibles:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))

                // Llistat d'habitacions dins de l'hotel
                hotel.rooms.forEach { room ->
                    RoomItem(room = room, onBookClick = { onBookClick(room.id) })
                }
            }
        }
    }
}

@Composable
fun RoomItem(room: Room, onBookClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imatge de l'habitació
            if (room.images.isNotEmpty()) {
                AsyncImage(
                    model = room.images.first(),
                    contentDescription = "Habitació",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(text = room.roomType, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Text(text = "${room.price} € / nit", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
            }

            Button(
                onClick = onBookClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Reservar")
            }
        }
    }
}

// Compliment estricte del PDF: DatePicker natiu de Material 3
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    OutlinedTextField(
        value = selectedDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "",
        onValueChange = { },
        label = { Text(label) },
        readOnly = true,
        leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
        modifier = modifier.clickable { showDialog = true },
        enabled = false,
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
                        onDateSelected(date)
                    }
                    showDialog = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel·lar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}