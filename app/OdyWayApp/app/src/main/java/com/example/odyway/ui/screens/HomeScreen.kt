package com.example.odyway.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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


// Mantenemos el mock de recomendaciones porque es estático por ahora
data class RecommendationMock(val id: Int, val title: String, val description: String)

val recomendacionesMock = listOf(
    RecommendationMock(1, "Descubre los Alpes", "Nieve y montañas espectaculares"),
    RecommendationMock(2, "Ruta del Sol", "Playas escondidas en Andalucía")
)

@Composable
fun HomeScreen(
    tripViewModel: TripViewModel,
    onCreateTripClick: () -> Unit,
    onSearchHotelClick: () -> Unit
) {
    // 1. Recolectamos el flujo de viajes desde el ViewModel
    val trips by tripViewModel.trips.collectAsState()
    val currentDate = LocalDate.now()

    // 2. Filtramos con lógica de negocio real!
    val activeTrip = trips.firstOrNull { it.isTripActive(currentDate) }
    val upcomingTrips = trips.filter { it.startDate.isAfter(currentDate) }

    Scaffold(
        topBar = {
            // ... (Tu código de la TopBar se queda exactamente igual) ...
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_odyway),
                        contentDescription = stringResource(R.string.home_logo_desc),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.app_name),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 30.sp
                    )
                }

                IconButton(onClick = onSearchHotelClick) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.home_search_desc),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateTripClick,
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.home_add_desc))
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Viaje en proceso (Solo se muestra si hay uno activo)
            if (activeTrip != null) {
                item {
                    SectionHeader(stringResource(R.string.home_section_in_progress), Icons.Default.FlightTakeoff)
                }
                item {
                    ActiveTripCard(activeTrip)
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Viajes planeados
            item {
                SectionHeader(stringResource(R.string.home_section_upcoming), Icons.Default.Event)
            }
            if (upcomingTrips.isEmpty()) {
                item { Text("No tienes viajes próximos. ¡Añade uno!") }
            } else {
                items(upcomingTrips) { trip ->
                    TripCard(trip)
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Recomendaciones
            item {
                SectionHeader(stringResource(R.string.home_section_recommendations), Icons.Default.Explore)
            }
            items(recomendacionesMock) { recom ->
                RecommendationCard(recom)
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}
@Composable
fun SectionHeader(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

// Actualizamos las Cards para recibir el dominio real (Trip)
@Composable
fun ActiveTripCard(trip: Trip) {
    val formatter = DateTimeFormatter.ofPattern("dd MMM")

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = trip.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Badge(containerColor = MaterialTheme.colorScheme.error) {
                    Text(
                        text = stringResource(R.string.home_live_badge),
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = trip.destination,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${trip.startDate.format(formatter)} - ${trip.endDate.format(formatter)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun TripCard(trip: Trip) {
    val formatter = DateTimeFormatter.ofPattern("dd MMM")

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text(
                text = trip.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = trip.destination,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${trip.startDate.format(formatter)} - ${trip.endDate.format(formatter)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun RecommendationCard(recom: RecommendationMock) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recom.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = recom.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
            Icon(
                imageVector = Icons.Default.Explore,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}


