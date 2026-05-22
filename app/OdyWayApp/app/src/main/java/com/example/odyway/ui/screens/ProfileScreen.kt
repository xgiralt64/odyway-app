package com.example.odyway.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.odyway.R
import com.example.odyway.domain.Reservation
import com.example.odyway.ui.viewmodels.AuthViewModel
import com.example.odyway.ui.viewmodels.HotelViewModel
import com.example.odyway.ui.viewmodels.SettingsViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    hotelViewModel: HotelViewModel = hiltViewModel(), // AÑADIDO
    authViewModel: AuthViewModel = hiltViewModel()    // AÑADIDO
) {
    val username by settingsViewModel.username.collectAsState()
    val birthDate by settingsViewModel.birthDate.collectAsState()

    // Estados de Firebase y la API de reservas
    val currentUser by authViewModel.currentUser.collectAsState()
    val reservations by hotelViewModel.reservations.collectAsState()
    val uiState by hotelViewModel.uiState.collectAsState()

    var reservationToCancel by remember { mutableStateOf<Reservation?>(null) }
    val context = LocalContext.current

    // Cargamos las reservas del usuario logueado usando su email real de Firebase (T4.1)
    LaunchedEffect(currentUser?.email) {
        currentUser?.email?.let { email ->
            hotelViewModel.loadReservations(email)
        }
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Modificamos el nombre de la primera pestaña a "Reserves"
    val tabs = listOf(
        "Reserves",
        stringResource(id = R.string.profile_tab_favorites),
        stringResource(id = R.string.profile_tab_stats)
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = stringResource(id = R.string.profile_settings_desc),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(38.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // CABECERA DEL PERFIL (Azul oscuro OdyWay)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(bottom = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Image(
                            painter = painterResource(id = R.drawable.icon_odyway),
                            contentDescription = stringResource(id = R.string.profile_pic_desc),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(124.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                        )
                        IconButton(
                            onClick = { /* TODO: Edit Photo */ },
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.error, CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = stringResource(id = R.string.profile_edit_desc),
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = username,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = currentUser?.email ?: "@odyway_user",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    if (birthDate > 0) {
                        val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
                        val birthDateDisplay = dateFormatter.format(Date(birthDate))

                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Cake,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = birthDateDisplay,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }

            // CONTADORES STATS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStatItem("Reserves", reservations.size.toString())
                ProfileStatItem(stringResource(id = R.string.profile_countries_label), "1")
                ProfileStatItem(stringResource(id = R.string.profile_photos_label), "0")
            }

            // TAB BAR (Línea indicadora roja corporativa)
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTabIndex == index) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // CONTENIDO DE LAS PESTAÑAS
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> {
                        // PESTAÑA 0: RESERVAS REALES (T4.1, T4.3)
                        if (uiState.isLoading) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else if (reservations.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No tens cap reserva activa", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(reservations) { reservation ->
                                    ProfileReservationCard(
                                        item = reservation,
                                        onDeleteClick = { reservationToCancel = reservation }
                                    )
                                }
                            }
                        }
                    }
                    1 -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(id = R.string.profile_no_favorites), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                    }
                    2 -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(id = R.string.profile_stats_coming_soon), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                    }
                }
            }
        }
    }

    // DIÁLOGO DE CONFIRMACIÓN PARA ELIMINAR RESERVA (T4.2)
    if (reservationToCancel != null) {
        AlertDialog(
            onDismissRequest = { reservationToCancel = null },
            title = { Text("Cancel·lar Reserva", fontWeight = FontWeight.Bold) },
            text = { Text("Segur que vols cancel·lar la reserva a l'hotel ${reservationToCancel!!.hotel?.name ?: reservationToCancel!!.hotel_id}? Aquesta acció eliminarà la reserva de la API del professor.") },
            confirmButton = {
                Button(
                    onClick = {
                        currentUser?.email?.let { email ->
                            hotelViewModel.cancelReservation(
                                resId = reservationToCancel!!.id,
                                email = email,
                                onSuccess = {
                                    Toast.makeText(context, "Reserva cancel·lada amb èxit!", Toast.LENGTH_SHORT).show()
                                    reservationToCancel = null
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { reservationToCancel = null }) {
                    Text("Enrere", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }
}

// COMPONENTE: Tarjeta de reserva adaptada (T4.3 - Muestra las imágenes del hotel)
@Composable
fun ProfileReservationCard(item: Reservation, onDeleteClick: () -> Unit) {
    val baseUrl = "http://15.224.84.148:8090"

    // Si la API devuelve ruta relativa, le añadimos la IP base del profesor
    val hotelImgUrl = item.hotel?.imageUrl?.let {
        if (it.startsWith("http")) it else baseUrl + it
    } ?: ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Imagen del hotel a la izquierda (T4.3)
            Image(
                painter = coil.compose.rememberAsyncImagePainter(model = hotelImgUrl),
                contentDescription = null,
                modifier = Modifier
                    .weight(0.35f)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )

            // Información en el centro
            Column(
                modifier = Modifier
                    .weight(0.53f)
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = item.hotel?.name ?: "Hotel (${item.hotel_id})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = "Habitació: ${item.room?.roomType?.replaceFirstChar { it.uppercase() } ?: item.room_id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${item.start_date} ➔ ${item.end_date}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "ID: ${item.id}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }

            // Botón de eliminar (papelera roja corporativa) a la derecha
            Box(
                modifier = Modifier
                    .weight(0.12f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Cancelar reserva",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}