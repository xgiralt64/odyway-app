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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.odyway.R
import com.example.odyway.ui.theme.MountainGreen
import com.example.odyway.ui.theme.OdyWayTheme

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Sort
import androidx.compose.ui.draw.clip

data class ActivityMock(
    val id: Int,
    val time: String,
    val title: String,
    val cost: Double
)
data class GalleryImageMock(val id: Int, val imageRes: Int)

val itinerarioMock = listOf(
    ActivityMock(1, "09:00", "Cafe i esmorzar tradicional", 12.50),
    ActivityMock(2, "10:30", "Visita guiada pel centre historic", 25.00),
    ActivityMock(3, "14:00", "Dinar a un restaurant local", 35.00),
    ActivityMock(4, "17:00", "Passeig lliure i compres", 0.00),
    ActivityMock(5, "20:00", "Sopar amb vistes a la ciutat", 45.00)
)

val galeriaMock = listOf(
    GalleryImageMock(1, R.drawable.paris_example),
    GalleryImageMock(2, R.drawable.paris_example),
    GalleryImageMock(3, R.drawable.paris_example),
    GalleryImageMock(4, R.drawable.paris_example),
    GalleryImageMock(5, R.drawable.paris_example),
    GalleryImageMock(6, R.drawable.paris_example)
)

@Composable
fun TripsScreen() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf(
        stringResource(id = R.string.trips_tab_itinerary),
        stringResource(id = R.string.trips_tab_gallery),
        stringResource(id = R.string.trips_tab_costs)
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp) // Altura de la imagen de cabecera
                ) {
                    // Imagen del viaje
                    Image(
                        painter = painterResource(id = R.drawable.paris_example),
                        contentDescription = "Imatge de París", // Hardcoded perque es contingut dinamic mock
                        contentScale = ContentScale.Crop, // Llena el Box recortando si es necesario
                        modifier = Modifier.fillMaxSize()
                    )

                    // Gradiente oscuro para que el texto sea legible
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)),
                                    startY = 150f
                                )
                            )
                    )

                    //título y fechas superpuestos en la esquina inferior izquierda
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Viatge a París", // Hardcoded perque es contingut dinamic mock
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Icono de calendario
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = stringResource(id = R.string.trips_dates_desc),
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // Fechas de ida y vuelta
                            Text(
                                text = "12 Oct - 15 Oct", // Hardcoded perque es contingut dinamic mock
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
                // --- hasta qui colocamos la imagen   ---

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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TripStatisticsSection()

            Spacer(modifier = Modifier.height(8.dp))

            when (selectedTabIndex) {
                0 -> ItineraryList()
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
fun TripStatisticsSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatItem(stringResource(id = R.string.trips_stat_budget), "1000€", MaterialTheme.colorScheme.onSurface)
            StatItem(stringResource(id = R.string.trips_stat_wasted), "117.50€", MaterialTheme.colorScheme.error)
            StatItem(stringResource(id = R.string.trips_stat_remaining), "882.50€", MountainGreen)
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
fun ItineraryList() {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(itinerarioMock) { activity ->
            ActivityCard(activity)
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun ActivityCard(activity: ActivityMock) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                    text = activity.time,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = activity.title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = stringResource(id = R.string.trips_cost_desc),
                    tint = MountainGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${activity.cost}€",
                    fontWeight = FontWeight.Bold,
                    color = MountainGreen
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

@Preview(showBackground = true, name = "Trips Mode Light")
@Composable
fun TripsScreenPreviewLight() {
    OdyWayTheme(darkTheme = false) {
        TripsScreen()
    }
}

@Preview(showBackground = true, name = "Trips Mode Dark")
@Composable
fun TripsScreenPreviewDark() {
    OdyWayTheme(darkTheme = true) {
        TripsScreen()
    }
}