package com.example.odyway.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.odyway.BuildConfig
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
    hotelViewModel: HotelViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val username by settingsViewModel.username.collectAsState()
    val birthDate by settingsViewModel.birthDate.collectAsState()

    val currentUser by authViewModel.currentUser.collectAsState()
    val reservations by hotelViewModel.reservations.collectAsState()
    val uiState by hotelViewModel.uiState.collectAsState()

    var reservationToCancel by remember { mutableStateOf<Reservation?>(null) }
    val context = LocalContext.current

    // Carga las reservas del usuario logueado
    LaunchedEffect(currentUser?.email) {
        currentUser?.email?.let { email ->
            hotelViewModel.loadReservations(email)
        }
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        stringResource(id = R.string.profile_tab_reserves),
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
            // Muestra la cabecera del perfil de usuario
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
                        text = currentUser?.email ?: stringResource(id = R.string.profile_default_user),
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

            // Muestra los contadores de estadisticas
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStatItem(stringResource(id = R.string.profile_tab_reserves), reservations.size.toString())
                ProfileStatItem(stringResource(id = R.string.profile_countries_label), "1")
                ProfileStatItem(stringResource(id = R.string.profile_photos_label), "0")
            }

            // Barra de pestañas principal
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

            // Controla el contenido visible de cada pestaña
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> {
                        if (uiState.isLoading) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else if (reservations.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = stringResource(id = R.string.profile_no_active_reserves),
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
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

    // Muestra el dialogo de confirmacion para eliminar una reserva
    if (reservationToCancel != null) {
        val hotelName = reservationToCancel!!.hotel?.name ?: reservationToCancel!!.hotel_id.toString()
        val successMessage = context.getString(R.string.profile_reserve_cancelled_success)

        AlertDialog(
            onDismissRequest = { reservationToCancel = null },
            title = { Text(stringResource(id = R.string.profile_cancel_reserve_title), fontWeight = FontWeight.Bold) },
            text = { Text(stringResource(id = R.string.profile_cancel_reserve_text, hotelName)) },
            confirmButton = {
                Button(
                    onClick = {
                        currentUser?.email?.let { email ->
                            hotelViewModel.cancelReservation(
                                resId = reservationToCancel!!.id,
                                hotelId = reservationToCancel!!.hotel_id,
                                email = email,
                                onSuccess = {
                                    Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
                                    reservationToCancel = null
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text(stringResource(id = R.string.profile_confirm_button)) }
            },
            dismissButton = {
                TextButton(onClick = { reservationToCancel = null }) {
                    Text(stringResource(id = R.string.profile_back_button), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }
}

// Muestra la tarjeta con la informacion de una reserva
@Composable
fun ProfileReservationCard(item: Reservation, onDeleteClick: () -> Unit) {
    val baseUrl = BuildConfig.HOTELS_API_URL

    val hotelImgUrl = item.hotel?.imageUrl?.let {
        if (it.startsWith("http")) it else baseUrl + it
    } ?: ""

    Card(
        modifier = Modifier.fillMaxWidth().height(140.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = coil.compose.rememberAsyncImagePainter(model = hotelImgUrl),
                contentDescription = null,
                modifier = Modifier.weight(0.35f).fillMaxHeight().background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.weight(0.53f).padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // Título
                    Text(
                        text = "${item.hotel?.name ?: item.hotel_id} - Room ${item.room_id}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${item.start_date} ➔ ${item.end_date}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Price: €${item.room?.price ?: "0.0"}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = item.guest_name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            Box(
                modifier = Modifier.weight(0.12f).fillMaxHeight(),
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