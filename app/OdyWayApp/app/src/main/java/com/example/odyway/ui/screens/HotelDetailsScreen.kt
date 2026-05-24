package com.example.odyway.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.odyway.BuildConfig
import com.example.odyway.R
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
            // Limpia el estado de error tras mostrarlo
            hotelViewModel.clearError()
        }
    }

    val baseUrl = BuildConfig.HOTELS_API_URL

    // Calcula el total de noches de la estancia
    var nights = 1L
    try {
        if (uiState.searchStartDate.isNotEmpty() && uiState.searchEndDate.isNotEmpty()) {
            val start = LocalDate.parse(uiState.searchStartDate)
            val end = LocalDate.parse(uiState.searchEndDate)
            nights = ChronoUnit.DAYS.between(start, end)
            if (nights <= 0) nights = 1L
        }
    } catch (e: Exception) {
        nights = 1L
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.hotel_details_title_format, hotel.name, hotel.id),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(id = R.string.hotel_details_back_desc))
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
                .padding(paddingValues)
                .padding(top = 12.dp)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Muestra el resumen de fechas y noches de la estancia
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                color = MaterialTheme.colorScheme.surface,
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
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(26.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(id = R.string.hotel_details_stay),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onBackground
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
                            text = stringResource(id = R.string.hotel_details_nights, nights),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Muestra la imagen principal del hotel
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

            // Muestra la lista de habitaciones disponibles
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(hotel.rooms) { room ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            val formattedRoomType = room.roomType.replaceFirstChar { it.uppercase() }
                            Text(
                                text = stringResource(id = R.string.hotel_details_room_title, formattedRoomType, room.id),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = stringResource(id = R.string.hotel_details_price_per_night, room.price.toString()),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Muestra la imagen de la habitacion si esta disponible
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

                            // Muestra el precio total y la accion de reserva
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = stringResource(id = R.string.hotel_details_total_price),
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = stringResource(id = R.string.hotel_details_price_format, totalPrice.toString()),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 20.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                                Button(
                                    onClick = { selectedRoom = room },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = MaterialTheme.colorScheme.onError
                                    ),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.hotel_details_reserve_button),
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Gestiona el dialogo de confirmacion de reserva
    if (selectedRoom != null) {
        val totalPrice = selectedRoom!!.price * nights
        val successMessage = context.getString(R.string.hotel_details_reserve_success)

        AlertDialog(
            onDismissRequest = { selectedRoom = null },
            title = { Text(stringResource(id = R.string.hotel_details_confirm_title), fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    stringResource(
                        id = R.string.hotel_details_confirm_text,
                        selectedRoom!!.roomType,
                        totalPrice.toString(),
                        hotel.name,
                        uiState.searchStartDate,
                        uiState.searchEndDate
                    )
                )
            },
            confirmButton = {
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
                                Toast.makeText(context, successMessage, Toast.LENGTH_LONG).show()
                                selectedRoom = null
                                onReservationComplete()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) { Text(stringResource(id = R.string.hotel_details_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { selectedRoom = null }) {
                    Text(stringResource(id = R.string.hotel_details_cancel), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }
}