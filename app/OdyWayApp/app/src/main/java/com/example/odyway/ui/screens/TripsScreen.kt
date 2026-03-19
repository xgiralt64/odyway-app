package com.example.odyway.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.odyway.R
import com.example.odyway.ui.theme.MountainGreen

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Sort
import androidx.compose.ui.draw.clip
import com.example.odyway.domain.ItineraryItem
import com.example.odyway.domain.Trip
import com.example.odyway.ui.viewmodels.TripViewModel
import java.time.format.DateTimeFormatter

// Mock de galería
data class GalleryImageMock(val id: Int, val imageRes: Int)
val galeriaMock = listOf(
    GalleryImageMock(1, R.drawable.paris_example),
    GalleryImageMock(2, R.drawable.paris_example)
)

@Composable
fun TripsScreen(tripViewModel: TripViewModel) {
    // 1. Obtenemos los viajes. Por ahora, asumimos que mostramos el primero.
    val trips by tripViewModel.trips.collectAsState()
    val currentTrip = trips.firstOrNull()

    // 2. Obtenemos el itinerario de ESE viaje
    val itinerary by tripViewModel.currentItinerary.collectAsState()

    // 3. Cuando la pantalla carga, le pedimos al ViewModel que busque las actividades de este viaje
    LaunchedEffect(currentTrip?.id) {
        currentTrip?.id?.let { tripViewModel.loadItineraryForTrip(it) }
    }

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf(
        stringResource(id = R.string.trips_tab_itinerary),
        stringResource(id = R.string.trips_tab_gallery),
        stringResource(id = R.string.trips_tab_costs)
    )

    if (currentTrip == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay viajes disponibles.")
        }
        return
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.paris_example),
                        contentDescription = "Imagen del viaje",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier.fillMaxSize().background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)),
                                startY = 150f
                            )
                        )
                    )
                    Column(
                        modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
                    ) {
                        Text(
                            text = currentTrip.title, // Ahora viene de la FakeDB
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = stringResource(id = R.string.trips_dates_desc),
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            val formatter = DateTimeFormatter.ofPattern("dd MMM")
                            Text(
                                text = "${currentTrip.startDate.format(formatter)} - ${currentTrip.endDate.format(formatter)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = MaterialTheme.colorScheme.tertiary
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
                                    color = if (selectedTabIndex == index) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            TripStatisticsSection(currentTrip)

            Spacer(modifier = Modifier.height(8.dp))

            when (selectedTabIndex) {
                0 -> ItineraryList(itinerary) // Pasamos la lista real
                1 -> TripGallerySection()
                2 -> Text(
                    text = stringResource(id = R.string.trips_costs_stats_placeholder),
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
fun TripStatisticsSection(trip: Trip) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatItem(stringResource(id = R.string.trips_stat_budget), "${trip.budget}€", MaterialTheme.colorScheme.onSurface)
            StatItem(stringResource(id = R.string.trips_stat_wasted), "0.0€", MaterialTheme.colorScheme.error)
            StatItem(stringResource(id = R.string.trips_stat_remaining), "${trip.budget}€", MountainGreen)
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun ItineraryList(itinerary: List<ItineraryItem>) {
    if (itinerary.isEmpty()) {
        Text("No hay actividades planificadas.", modifier = Modifier.padding(16.dp))
    } else {
        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(itinerary) { activity ->
                ActivityCard(activity)
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun ActivityCard(activity: ItineraryItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = stringResource(id = R.string.trips_hour_desc),
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = activity.time.toString(), // Viene de LocalTime en FakeDB
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = activity.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}
@Composable
fun TripGallerySection() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Barra superior de la galería: Título, Botón Ordenar y Botón Añadir
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.trips_gallery_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row {
                // Botón Ordenar (Mock)
                IconButton(onClick = { /* TODO: Lógica de ordenar */ }) {
                    Icon(
                        imageVector = Icons.Default.Sort,
                        contentDescription = stringResource(id = R.string.trips_sort_desc),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                // Botón Añadir Foto (Mock)
                IconButton(onClick = { /* TODO: Lógica de añadir foto */ }) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = stringResource(id = R.string.trips_add_photo_desc),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Cuadrícula de fotos (Grid)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // 2 columnas
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(galeriaMock) { image ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f) // Hace que la imagen sea perfectamente cuadrada
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    // La imagen
                    Image(
                        painter = painterResource(id = image.imageRes),
                        contentDescription = stringResource(id = R.string.trips_photo_desc),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    //btn borrar superpuesto en la esquina superior derecha
                    IconButton(
                        onClick = { /* TODO: Lógica de borrar foto */ },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape) // Fondo semitransparente para que se vea
                            .size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(id = R.string.trips_delete_desc),
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            // Margen inferior para que no quede tapado por la barra de navegacion
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

//@Preview(showBackground = true, name = "Trips Mode Light")
//@Composable
//fun TripsScreenPreviewLight() {
//    OdyWayTheme(darkTheme = false) {
//        TripsScreen()
//    }
//}
//
//@Preview(showBackground = true, name = "Trips Mode Dark")
//@Composable
//fun TripsScreenPreviewDark() {
//    OdyWayTheme(darkTheme = true) {
//        TripsScreen()
//    }
//}