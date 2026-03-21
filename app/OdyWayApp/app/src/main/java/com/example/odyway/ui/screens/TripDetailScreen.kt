package com.example.odyway.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.odyway.R
import com.example.odyway.domain.ItineraryItem
import com.example.odyway.domain.Trip
import com.example.odyway.ui.viewmodels.TripViewModel
import java.time.format.DateTimeFormatter

// Mock de galería
data class GalleryImageMock(val id: Int, val imageRes: Int)
val galeriaMock = listOf(
    GalleryImageMock(1, R.drawable.paris_example),
    GalleryImageMock(2, R.drawable.paris_example),
    GalleryImageMock(3, R.drawable.paris_example),
    GalleryImageMock(4, R.drawable.paris_example)
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TripDetailScreen(
    tripId: String, // <-- 1. AÑADIMOS ESTE PARÁMETRO
    tripViewModel: TripViewModel,
    onNavigateBack: () -> Unit,
    onModifyItineraryClick: (String) -> Unit = {},
    onEditTripClick: () -> Unit = {}
) {
    val trips by tripViewModel.trips.collectAsState()

    // <-- 2. BUSCAMOS EL VIAJE EXACTO POR SU ID -->
    val currentTrip = trips.find { it.id == tripId }

    val itinerary by tripViewModel.currentItinerary.collectAsState()

    // ESTADO PARA EL DIÁLOGO DE BORRAR
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(currentTrip?.id) {
        currentTrip?.id?.let { tripViewModel.loadItineraryForTrip(it) }
    }

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf(
        stringResource(id = R.string.trips_tab_itinerary),
        stringResource(id = R.string.trips_tab_costs),
        stringResource(id = R.string.trips_tab_gallery)
    )

    if (currentTrip == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Cargando viaje...")
        }
        return
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            item {
                TripDetailHeader(
                    trip = currentTrip,
                    onNavigateBack = onNavigateBack,
                    onEditTripClick = onEditTripClick,
                    onDeleteTripClick = { showDeleteDialog = true } // Activamos el Diálogo
                )
            }

            stickyHeader {
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
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTabIndex == index) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        )
                    }
                }
            }

            when (selectedTabIndex) {
                0 -> itineraryTabContent(itinerary, currentTrip.id, onModifyItineraryClick)
                1 -> costsTabContent(currentTrip, itinerary)
                2 -> galleryTabContent(galeriaMock)
            }
        }
    }

    // DIÁLOGO DE CONFIRMACIÓN PARA BORRAR VIAJE
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Viatge", fontWeight = FontWeight.Bold) },
            text = { Text("Estàs segur que vols eliminar aquest viatge i tot el seu itinerari? Aquesta acció no es pot desfer.") },
            confirmButton = {
                TextButton(onClick = {
                    tripViewModel.deleteTrip(currentTrip.id)
                    showDeleteDialog = false
                    onNavigateBack() // ¡Súper importante! Nos devuelve a la lista de viajes
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel·lar") }
            }
        )
    }
}

@Composable
fun TripDetailHeader(trip: Trip, onNavigateBack: () -> Unit, onEditTripClick: () -> Unit, onDeleteTripClick: () -> Unit) {
    // ESTADO PARA EL MENÚ DESPLEGABLE
    var expanded by remember { mutableStateOf(false) }

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

        // BOTÓN DE VOLVER ATRÁS (IZQUIERDA)
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 25.dp, start = 8.dp)
                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Tornar enrere", tint = Color.White)
        }

        // MENÚ DE TRES PUNTOS (DERECHA - SIMÉTRICO Y ELEGANTE)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 25.dp, end = 8.dp)
        ) {
            IconButton(
                onClick = { expanded = true },
                modifier = Modifier.background(Color.Black.copy(alpha = 0.4f), CircleShape)
            ) {
                Icon(Icons.Default.MoreVert, tint = Color.White, contentDescription = "Opcions")
            }

            // El desplegable igual que en la lista
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Editar") },
                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    onClick = {
                        expanded = false
                        onEditTripClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Eliminar", color = MaterialTheme.colorScheme.error) },
                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    onClick = {
                        expanded = false
                        onDeleteTripClick()
                    }
                )
            }
        }

        // TÍTULO Y FECHAS
        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
        ) {
            Text(
                text = trip.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
                Text(
                    text = "${trip.startDate.format(formatter)} - ${trip.endDate.format(formatter)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

fun androidx.compose.foundation.lazy.LazyListScope.itineraryTabContent(
    itinerary: List<ItineraryItem>,
    tripId: String,
    onModifyItineraryClick: (String) -> Unit
) {
    // Cabecera del Itinerario
    item {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Resum del Itinerari", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Row {
                IconButton(onClick = { /* TODO: Ordenar */ }) { Icon(Icons.Default.Sort, contentDescription = "Ordenar", tint = MaterialTheme.colorScheme.primary) }
                IconButton(onClick = { onModifyItineraryClick(tripId) }) { Icon(Icons.Default.Edit, contentDescription = "Modificar Itinerari", tint = MaterialTheme.colorScheme.error) }
            }
        }
    }

    if (itinerary.isEmpty()) {
        item { Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) { Text("No tens activitats planificades.", color = Color.Gray) } }
    } else {
        val groupedItinerary = itinerary.groupBy { it.date }
        val dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM")
        groupedItinerary.forEach { (date, activities) ->
            item { Text(text = date.format(dateFormatter).replaceFirstChar { it.uppercase() }, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) }
            items(activities) { activity -> ActivityCardSummary(activity) }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
    item { Spacer(modifier = Modifier.height(60.dp)) }
}

@Composable
fun ActivityCardSummary(activity: ItineraryItem) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(55.dp)) {
            Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
            Text(text = activity.time.toString(), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = activity.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = activity.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
    }
    Divider(modifier = Modifier.padding(start = 80.dp, end = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))
}

fun androidx.compose.foundation.lazy.LazyListScope.costsTabContent(trip: Trip, itinerary: List<ItineraryItem>) {
    val totalSpent = 117.50 // Simulado
    val progress = (totalSpent / trip.budget).toFloat()
    item { Spacer(modifier = Modifier.height(16.dp)) }
    item {
        Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "$totalSpent €", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape), color = MaterialTheme.colorScheme.error, trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Pressupost: ${trip.budget} €", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
            }
        }
    }
    item { Text(text = "Desglossament", modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium) }
    items(itinerary) { activity ->
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Restaurant, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = activity.title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text(text = "Activitat", fontSize = 12.sp, color = Color.Gray)
            }
            Text(text = "35.00 €", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
        }
        Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))
    }
    item { Spacer(modifier = Modifier.height(60.dp)) }
}

fun androidx.compose.foundation.lazy.LazyListScope.galleryTabContent(images: List<GalleryImageMock>) {
    item {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(id = R.string.trips_gallery_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Row {
                IconButton(onClick = { /* TODO: Ordenar */ }) { Icon(Icons.Default.Sort, contentDescription = "Ordenar", tint = MaterialTheme.colorScheme.primary) }
                IconButton(onClick = { /* TODO: Añadir foto */ }) { Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Afegeix foto", tint = MaterialTheme.colorScheme.error) }
            }
        }
    }
    items(images.chunked(2)) { rowImages ->
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (image in rowImages) {
                Box(modifier = Modifier.weight(1f).aspectRatio(1f).clip(RoundedCornerShape(8.dp))) {
                    Image(painter = painterResource(id = image.imageRes), contentDescription = "Foto", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    IconButton(onClick = { /* TODO: Borrar foto */ }, modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape).size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Esborrar", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
            if (rowImages.size == 1) Spacer(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
    item { Spacer(modifier = Modifier.height(80.dp)) }
}