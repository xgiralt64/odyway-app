package com.example.odyway.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.odyway.R
import com.example.odyway.domain.Trip
import com.example.odyway.ui.viewmodels.TripViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(
    tripViewModel: TripViewModel,
    onTripClick: (String) -> Unit = {},
    onCreateTripClick: () -> Unit = {}
) {
    // 1. Obtenemos los viajes reales desde el ViewModel
    val allTrips by tripViewModel.trips.collectAsState()
    val currentDate = LocalDate.now()

    // 2. Estado para las pestañas de filtrado
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Tots", "Pròxims", "Passats") // Usamos catalán según tu base

    // 3. Lógica de filtrado dinámico
    val filteredTrips = when (selectedTabIndex) {
        1 -> allTrips.filter { it.startDate.isAfter(currentDate) || it.isTripActive(currentDate) } // Próximos o en curso
        2 -> allTrips.filter { it.endDate.isBefore(currentDate) } // Ya terminados
        else -> allTrips // Todos (0)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.nav_trips), // "Els meus Viatges"
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateTripClick,
                containerColor = MaterialTheme.colorScheme.error, // MapPinRed
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 16.dp, end = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Afegeix Viatge", modifier = Modifier.size(32.dp))
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Pestañas de filtrado
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == index) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                            )
                        }
                    )
                }
            }

            // Lista de viajes
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                if (filteredTrips.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "No hi ha viatges en aquesta categoria.",
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    }
                } else {
                    items(filteredTrips) { trip ->
                        TripCardList(trip = trip, onClick = { onTripClick(trip.id) })
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) } // Espacio para la BottomBar
            }
        }
    }
}

@Composable
fun TripCardList(trip: Trip, onClick: () -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onClick() }, // Hacemos la tarjeta clicable
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Imagen representativa del viaje a la izquierda
            Image(
                painter = painterResource(id = R.drawable.paris_example), // Mock: En el futuro puede venir de trip.coverImage
                contentDescription = "Imatge del destí",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(120.dp)
            )

            // Detalles del viaje a la derecha
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = trip.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "📍 ${trip.destination}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 1
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Fechas
                    Text(
                        text = "📅 ${trip.startDate.format(formatter)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )

                    // Presupuesto
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Pressupost",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            fontSize = 10.sp
                        )
                        Text(
                            text = "${trip.budget}€",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error, // Usamos tu rojo para destacar
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}