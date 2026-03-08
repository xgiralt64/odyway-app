package com.example.odyway.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.odyway.R
import com.example.odyway.ui.theme.OdyWayTheme

// Modelos mock
data class TripMock(
    val id: Int,
    val title: String,
    val destination: String,
    val date: String
)

data class RecommendationMock(
    val id: Int,
    val title: String,
    val description: String
)

val viajeEnCursoMock = TripMock(0, "Escapada Express", "Roma, Italia", "En curso - Finaliza 12 Mar")

val misViajesMock = listOf(
    TripMock(1, "Asadito de fin de semana", "París, Francia", "12 Oct - 15 Oct"),
    TripMock(2, "Aventura Asiática", "Kioto, Japón", "01 Nov - 15 Nov"),
    TripMock(3, "Ruta en Coche al buffet", "Costa Brava, España", "20 Ago - 25 Ago")
)

val recomendacionesMock = listOf(
    RecommendationMock(1, "Descubre los Alpes", "Nieve y montañas espectaculares"),
    RecommendationMock(2, "Ruta del Sol", "Playas escondidas en Andalucía")
)

@Composable
fun HomeScreen() {
    Scaffold(
        // El Scaffold toma automáticamente el color "background" del tema
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    // Usamos primary (NavyBlue en ambos temas para mantener la identidad)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_odyway),
                        contentDescription = "Logo OdyWay",
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "OdyWay",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        // onPrimary siempre será blanco para contrastar con la barra
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 30.sp
                    )
                }

                IconButton(onClick = { /* TODO: Acción de búsqueda */ }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar viaje",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Acción para el botón + */ },
                containerColor = MaterialTheme.colorScheme.error, // MapPinRed
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "add")
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

            // Viaje en proceso
            item {
                SectionHeader("En progreso", Icons.Default.FlightTakeoff)
            }
            item {
                ActiveTripCard(viajeEnCursoMock)
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Viajes planeados
            item {
                SectionHeader("Próximos viajes", Icons.Default.Event)
            }
            items(misViajesMock) { trip ->
                TripCard(trip)
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Recomendaciones
            item {
                SectionHeader("Recomendaciones para ti", Icons.Default.Explore)
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

@Composable
fun ActiveTripCard(trip: TripMock) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
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
                    Text("LIVE", color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(horizontal = 4.dp))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${trip.destination}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${trip.date}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = 0.4f,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
fun TripCard(trip: TripMock) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        // Automáticamente será White en modo Claro y NavyBlue en modo Oscuro
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = trip.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                // Automáticamente oscuro en Light y blanco en Dark
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${trip.destination}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${trip.date}",
                style = MaterialTheme.typography.bodySmall,
                // Usamos la opacidad para el texto gris, es un estándar en Material
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

@Preview(showBackground = true, name = "Home screen Light")
@Composable
fun HomeScreenPreviewLight() {
    OdyWayTheme(darkTheme = false) {
        HomeScreen()
    }
}

@Preview(showBackground = true, name = "Home screen Dark")
@Composable
fun HomeScreenPreviewDark() {
    OdyWayTheme(darkTheme = true) {
        HomeScreen()
    }
}