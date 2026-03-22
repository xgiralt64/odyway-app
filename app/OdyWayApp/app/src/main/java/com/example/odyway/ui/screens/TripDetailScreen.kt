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
import androidx.compose.ui.tooling.preview.Preview
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
    tripId: String,
    tripViewModel: TripViewModel,
    onNavigateBack: () -> Unit,
    onModifyItineraryClick: (String) -> Unit = {},
    onEditTripClick: () -> Unit = {}
) {
    val trips by tripViewModel.trips.collectAsState()
    val currentTrip = trips.find { it.id == tripId }
    val itinerary by tripViewModel.currentItinerary.collectAsState()

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
            Text(stringResource(R.string.trip_loading), color = MaterialTheme.colorScheme.onBackground)
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
                    onDeleteTripClick = { showDeleteDialog = true }
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

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.dialog_delete_trip_title), fontWeight = FontWeight.Bold) },
            text = { Text(stringResource(R.string.dialog_delete_trip_text)) },
            confirmButton = {
                TextButton(onClick = {
                    tripViewModel.deleteTrip(currentTrip.id)
                    showDeleteDialog = false
                    onNavigateBack()
                }) { Text(stringResource(R.string.action_delete), color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text(stringResource(R.string.action_cancel)) }
            }
        )
    }
}

@Composable
fun TripDetailHeader(trip: Trip, onNavigateBack: () -> Unit, onEditTripClick: () -> Unit, onDeleteTripClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
        Image(
            painter = painterResource(id = R.drawable.paris_example),
            contentDescription = stringResource(R.string.trips_photo_desc),
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

        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.align(Alignment.TopStart).padding(top = 25.dp, start = 8.dp).background(Color.Black.copy(alpha = 0.4f), CircleShape)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back_button), tint = Color.White)
        }

        Box(modifier = Modifier.align(Alignment.TopEnd).padding(top = 25.dp, end = 8.dp)) {
            IconButton(
                onClick = { expanded = true },
                modifier = Modifier.background(Color.Black.copy(alpha = 0.4f), CircleShape)
            ) {
                Icon(Icons.Default.MoreVert, tint = Color.White, contentDescription = stringResource(R.string.action_options))
            }

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.action_edit)) },
                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    onClick = { expanded = false; onEditTripClick() }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.action_delete), color = MaterialTheme.colorScheme.error) },
                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    onClick = { expanded = false; onDeleteTripClick() }
                )
            }
        }

        Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
            Text(text = trip.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
                Text(text = "${trip.startDate.format(formatter)} - ${trip.endDate.format(formatter)}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

fun androidx.compose.foundation.lazy.LazyListScope.itineraryTabContent(
    itinerary: List<ItineraryItem>,
    tripId: String,
    onModifyItineraryClick: (String) -> Unit
) {
    item {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.itinerary_summary_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Row {
                IconButton(onClick = { /* TODO: Ordenar */ }) { Icon(Icons.Default.Sort, contentDescription = stringResource(R.string.trips_sort_desc), tint = MaterialTheme.colorScheme.secondary) } // Usamos secondary en vez de primary para modo oscuro
                IconButton(onClick = { onModifyItineraryClick(tripId) }) { Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.action_modify_itinerary), tint = MaterialTheme.colorScheme.error) }
            }
        }
    }

    if (itinerary.isEmpty()) {
        item {
            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.itinerary_empty), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) // Adaptativo
            }
        }
    } else {
        val groupedItinerary = itinerary.groupBy { it.date }
        val dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM")
        groupedItinerary.forEach { (date, activities) ->
            item {
                Text(
                    text = date.format(dateFormatter).replaceFirstChar { it.uppercase() },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary // Secondary brilla mejor en modo oscuro
                )
            }
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
            Text(text = activity.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) // Adaptativo
        }
    }
    Divider(modifier = Modifier.padding(start = 80.dp, end = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
}

fun androidx.compose.foundation.lazy.LazyListScope.costsTabContent(trip: Trip, itinerary: List<ItineraryItem>) {
    val totalSpent = 117.50
    val progress = (totalSpent / trip.budget).toFloat()

    item { Spacer(modifier = Modifier.height(16.dp)) }
    item {
        Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "$totalSpent €", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape), color = MaterialTheme.colorScheme.error, trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "${stringResource(R.string.trips_stat_budget)}: ${trip.budget} €", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
            }
        }
    }
    item {
        Text(
            text = stringResource(R.string.costs_breakdown),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
    items(itinerary) { activity ->
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Restaurant, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp)) // Adaptativo
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = activity.title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text(text = stringResource(R.string.costs_activity_label), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) // Adaptativo
            }
            Text(text = "35.00 €", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
        }
        Divider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
    }
    item { Spacer(modifier = Modifier.height(60.dp)) }
}

fun androidx.compose.foundation.lazy.LazyListScope.galleryTabContent(images: List<GalleryImageMock>) {
    item {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(id = R.string.trips_gallery_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Row {
                IconButton(onClick = { /* TODO: Ordenar */ }) { Icon(Icons.Default.Sort, contentDescription = stringResource(R.string.trips_sort_desc), tint = MaterialTheme.colorScheme.secondary) } // Secundario para mejor visibilidad oscura
                IconButton(onClick = { /* TODO: Añadir foto */ }) { Icon(Icons.Default.AddPhotoAlternate, contentDescription = stringResource(R.string.trips_add_photo_desc), tint = MaterialTheme.colorScheme.error) }
            }
        }
    }
    items(images.chunked(2)) { rowImages ->
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (image in rowImages) {
                Box(modifier = Modifier.weight(1f).aspectRatio(1f).clip(RoundedCornerShape(8.dp))) {
                    Image(painter = painterResource(id = image.imageRes), contentDescription = stringResource(R.string.trips_photo_desc), contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    IconButton(onClick = { /* TODO: Borrar foto */ }, modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape).size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.trips_delete_desc), tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
            if (rowImages.size == 1) Spacer(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
    item { Spacer(modifier = Modifier.height(80.dp)) }
}

@Preview(showBackground = true, name = "Trip Detail - Light Mode")
@Composable
fun TripDetailScreenPreviewLight() {
    com.example.odyway.ui.theme.OdyWayTheme(darkTheme = false) {
        // NOTA: En un entorno real, aquí pasaríamos un MockViewModel.
        // Si la preview crashea por el ViewModel, la práctica recomendada es
        // extraer la UI pura a una función "Stateless" sin el ViewModel.
        TripDetailScreen(
            tripId = "trip-001",
            tripViewModel = androidx.lifecycle.viewmodel.compose.viewModel(), // Puede requerir tu Factory
            onNavigateBack = {},
            onModifyItineraryClick = {},
            onEditTripClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Trip Detail - Dark Mode")
@Composable
fun TripDetailScreenPreviewDark() {
    com.example.odyway.ui.theme.OdyWayTheme(darkTheme = true) {
        TripDetailScreen(
            tripId = "trip-001",
            tripViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
            onNavigateBack = {},
            onModifyItineraryClick = {},
            onEditTripClick = {}
        )
    }
}