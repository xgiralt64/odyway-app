package com.example.odyway.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.odyway.domain.Room
import com.example.odyway.ui.viewmodels.HotelViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailsScreen(
    hotelId: String,
    hotelViewModel: HotelViewModel,
    onNavigateBack: () -> Unit,
    userEmail: String,
    userName: String,
    onReservationComplete: () -> Unit
) {
    val uiState by hotelViewModel.uiState.collectAsState()
    val context = LocalContext.current

    val hotel = uiState.hotels.find { it.id == hotelId }
    var selectedRoom by remember { mutableStateOf<Room?>(null) }

    if (hotel == null) return

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            Toast.makeText(context, uiState.errorMessage, Toast.LENGTH_LONG).show()
            hotelViewModel.clearError() // Limpiamos el error después de mostrarlo
        }
    }

    val baseUrl = "http://15.224.84.148:8090"

    // CÁLCULO DE LAS NOCHES
    var nights = 1L
    try {
        if (uiState.searchStartDate.isNotEmpty() && uiState.searchEndDate.isNotEmpty()) {
            val start = LocalDate.parse(uiState.searchStartDate)
            val end = LocalDate.parse(uiState.searchEndDate)
            nights = ChronoUnit.DAYS.between(start, end)
            if (nights <= 0) nights = 1L // Mínimo 1 noche
        }
    } catch (e: Exception) {
        nights = 1L
    }

    Scaffold(
        topBar = {
            // Aplicamos los colores corporativos: Barra azul oscuro, texto e iconos blancos
            TopAppBar(
                title = { Text("${hotel.name} (${hotel.id})", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Volver") }
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
                .padding(paddingValues)
                .padding(top = 12.dp)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Banner de la estancia (Fondo suave para destacarlo)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                color = MaterialTheme.colorScheme.surface, // blanco en tema claro
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {

                        Text(
                            text = "Estada",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Text(
                                text = uiState.searchStartDate,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = uiState.searchEndDate,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "$nights nits",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }



            // FOTO GRANDE DEL HOTEL con un ligero borde redondeado por abajo
            val hotelImgUrl = if (hotel.imageUrl.startsWith("http")) hotel.imageUrl else baseUrl + hotel.imageUrl
            Image(
                painter = coil.compose.rememberAsyncImagePainter(model = hotelImgUrl),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                    .padding(horizontal = 16.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(hotel.rooms) { room ->
                    // Tarjetas de habitaciones con elevación para que destaquen
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Título de la habitación
                            Text(
                                text = "${room.roomType.replaceFirstChar { it.uppercase() }} (${room.id})",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${room.price} € / nit",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // FOTO DE LA HABITACIÓN
                            if (room.images.isNotEmpty()) {
                                val roomImgUrl = if (room.images.first().startsWith("http")) room.images.first() else baseUrl + room.images.first()
                                Image(
                                    painter = coil.compose.rememberAsyncImagePainter(model = roomImgUrl),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            val totalPrice = room.price * nights
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Precio total en azul corporativo
                                Column {
                                    Text(text = "Preu Total", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        text = "€$totalPrice",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 20.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                // Botón en rojo (error)
                                Button(
                                    onClick = { selectedRoom = room },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = MaterialTheme.colorScheme.onError
                                    ),
                                    shape = RoundedCornerShape(50) // Botón muy redondeado
                                ) {
                                    Text("Reservar", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedRoom != null) {
        val totalPrice = selectedRoom!!.price * nights

        AlertDialog(
            onDismissRequest = { selectedRoom = null },
            title = { Text("Confirmar Reserva", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Vols confirmar la reserva de l'habitació ${selectedRoom!!.roomType} per un total de $totalPrice€ a l'hotel ${hotel.name} des del ${uiState.searchStartDate} fins al ${uiState.searchEndDate}?"
                )
            },
            confirmButton = {
                // Botón de confirmar en rojo
                Button(
                    onClick = {
                        hotelViewModel.reserveRoom(
                            hotelId = hotel.id,
                            roomId = selectedRoom!!.id,
                            guestName = userName,
                            guestEmail = userEmail,
                            hotelName = hotel.name,
                            roomType = selectedRoom!!.roomType,
                            price = totalPrice,
                            onSuccess = {
                                Toast.makeText(context, "Reserva confirmada i guardada!", Toast.LENGTH_LONG).show()
                                selectedRoom = null
                                onReservationComplete()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { selectedRoom = null }) {
                    Text("Cancel·lar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }
}