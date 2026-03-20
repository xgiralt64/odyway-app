package com.example.odyway.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.odyway.R
import com.example.odyway.domain.ItineraryItem
import com.example.odyway.domain.Trip
import com.example.odyway.ui.viewmodels.TripViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

// =======================================================
// DICCIONARIO CENTRAL DE CATEGORÍAS (Iconos y Colores)
// =======================================================
data class CategoryData(val name: String, val icon: ImageVector, val color: Color)

val categoryList = listOf(
    CategoryData("Visita", Icons.Default.CameraAlt, Color(0xFF9C27B0)),       // Morado
    CategoryData("Menjar", Icons.Default.Restaurant, Color(0xFFFF9800)),      // Naranja
    CategoryData("Transport", Icons.Default.DirectionsBus, Color(0xFF2196F3)), // Azul
    CategoryData("Allotjament", Icons.Default.Hotel, Color(0xFF4CAF50)),      // Verde
    CategoryData("Altres", Icons.Default.Star, Color(0xFFFFC107))             // Amarillo
)

// Helper para encontrar la info de una categoría por nombre
fun getCategoryData(name: String): CategoryData {
    return categoryList.find { it.name == name } ?: categoryList.last()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryEditScreen(
    tripId: String,
    tripViewModel: TripViewModel,
    onNavigateBack: () -> Unit
) {
    val trips by tripViewModel.trips.collectAsState()
    val trip = trips.find { it.id == tripId }
    val fullItinerary by tripViewModel.currentItinerary.collectAsState()

    if (trip == null) {
        LaunchedEffect(Unit) { onNavigateBack() }
        return
    }

    val daysCount = ChronoUnit.DAYS.between(trip.startDate, trip.endDate).toInt() + 1
    val tripDays = List(daysCount) { trip.startDate.plusDays(it.toLong()) }
    val itineraryByDay = fullItinerary.groupBy { it.date }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var expandedDays by remember { mutableStateOf(tripDays.toSet()) }

    var showFormDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<ItineraryItem?>(null) }
    var selectedDateForNewActivity by remember { mutableStateOf(tripDays.first()) }
    var suggestedTimeForNewActivity by remember { mutableStateOf(LocalTime.of(9, 0)) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(stringResource(R.string.itinerary_edit_title), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary) }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
                )

                ScrollableTabRow(
                    selectedTabIndex = 0,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    edgePadding = 8.dp,
                    indicator = { }
                ) {
                    val formatter = DateTimeFormatter.ofPattern("dd MMM")
                    tripDays.forEachIndexed { index, date ->
                        Tab(
                            selected = false,
                            onClick = {
                                coroutineScope.launch {
                                    expandedDays = expandedDays + date
                                    listState.animateScrollToItem(index)
                                }
                            },
                            text = {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Día ${index + 1}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(date.format(formatter), fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f))
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(paddingValues).background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(tripDays) { index, date ->
                val isExpanded = expandedDays.contains(date)
                val dailyActivities = itineraryByDay[date]?.sortedBy { it.time } ?: emptyList()

                DayHeaderCard(
                    dayNumber = index + 1,
                    date = date,
                    isExpanded = isExpanded,
                    onClick = { expandedDays = if (isExpanded) expandedDays - date else expandedDays + date }
                )

                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically(animationSpec = tween(300)),
                    exit = shrinkVertically(animationSpec = tween(300))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, bottom = 16.dp, top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (dailyActivities.isEmpty()) {
                            Text("Cap activitat. Afegeix alguna cosa!", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(start = 16.dp, bottom = 8.dp))
                        } else {
                            dailyActivities.forEach { activity ->
                                CategoryActivityCard(
                                    activity = activity,
                                    onEditClick = {
                                        itemToEdit = activity
                                        selectedDateForNewActivity = activity.date
                                        suggestedTimeForNewActivity = activity.time
                                        showFormDialog = true
                                    },
                                    onDeleteClick = { tripViewModel.deleteItineraryItem(activity.id) }
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                itemToEdit = null
                                selectedDateForNewActivity = date
                                val lastActivityTime = dailyActivities.maxByOrNull { it.time }?.time
                                suggestedTimeForNewActivity = lastActivityTime?.plusHours(1) ?: LocalTime.of(10, 0)
                                showFormDialog = true
                            },
                            modifier = Modifier.fillMaxWidth().height(45.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.add_activity_button))
                        }
                    }
                }
            }
        }
    }

    if (showFormDialog) {
        ActivityFormFullScreenDialog(
            trip = trip,
            dateAssigned = selectedDateForNewActivity,
            suggestedTime = suggestedTimeForNewActivity,
            itemToEdit = itemToEdit,
            onDismiss = { showFormDialog = false },
            onSave = { newItem ->
                if (itemToEdit == null) tripViewModel.addItineraryItem(trip, newItem)
                else tripViewModel.updateItineraryItem(trip, newItem)
                showFormDialog = false
            }
        )
    }
}

@Composable
fun DayHeaderCard(dayNumber: Int, date: LocalDate, isExpanded: Boolean, onClick: () -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMM")
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Día $dayNumber", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                Text(text = date.format(formatter).replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f))
            }
            Icon(imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun CategoryActivityCard(activity: ItineraryItem, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    // Usamos el Helper centralizado
    val catData = getCategoryData(activity.category)

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(text = activity.time.toString(), fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, modifier = Modifier.width(55.dp))
        Spacer(modifier = Modifier.width(8.dp))

        Card(
            modifier = Modifier.weight(1f).clickable { onEditClick() },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                Box(modifier = Modifier.width(8.dp).fillMaxHeight().background(catData.color))
                Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(42.dp).background(catData.color.copy(alpha = 0.15f), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(catData.icon, contentDescription = null, tint = catData.color)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = activity.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(text = "📍 ${activity.location}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                    IconButton(onClick = onDeleteClick, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
                    }
                }
            }
        }
    }
}

// =======================================================
// FORMULARIO CON SELECTOR DE CATEGORÍA "CARRUSEL"
// =======================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityFormFullScreenDialog(
    trip: Trip,
    dateAssigned: LocalDate,
    suggestedTime: LocalTime,
    itemToEdit: ItineraryItem?,
    onDismiss: () -> Unit,
    onSave: (ItineraryItem) -> Unit
) {
    var title by remember { mutableStateOf(itemToEdit?.title ?: "") }
    var location by remember { mutableStateOf(itemToEdit?.location ?: "") }
    var description by remember { mutableStateOf(itemToEdit?.description ?: "") }
    var time by remember { mutableStateOf(suggestedTime) }
    var category by remember { mutableStateOf(itemToEdit?.category ?: "Visita") }

    var showTimePicker by remember { mutableStateOf(false) }
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(if (itemToEdit == null) "Nova Activitat" else "Editar Activitat", fontWeight = FontWeight.Bold)
                            Text(dateAssigned.format(formatter), fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Cancelar") }
                    },
                    actions = {
                        TextButton(onClick = {
                            onSave(ItineraryItem(
                                id = itemToEdit?.id ?: UUID.randomUUID().toString(),
                                tripId = trip.id, date = dateAssigned, time = time, title = title,
                                description = description, location = location, category = category
                            ))
                        }) {
                            Text(stringResource(R.string.save_button), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(top = 16.dp, start = 20.dp, end = 20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text(stringResource(R.string.activity_name_hint)) }, modifier = Modifier.fillMaxWidth(), singleLine = true)

                // =======================================================
                // NUEVO CARRUSEL DE CATEGORÍAS (Visual y Escalable)
                // =======================================================
                Column {
                    Text("Categoria:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categoryList) { catData ->
                            val isSelected = category == catData.name
                            Surface(
                                onClick = { category = catData.name },
                                shape = RoundedCornerShape(16.dp),
                                // Si está seleccionado, se pinta entero. Si no, es transparente con el borde
                                color = if (isSelected) catData.color else MaterialTheme.colorScheme.surface,
                                border = if (!isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)) else null,
                                modifier = Modifier.height(40.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = catData.icon,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else catData.color,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = catData.name,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }

                OutlinedCard(onClick = { showTimePicker = true }, modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, tint = MaterialTheme.colorScheme.error, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Hora: $time", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }

                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text(stringResource(R.string.activity_loc_hint)) }, modifier = Modifier.fillMaxWidth(), leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text(stringResource(R.string.activity_desc_hint)) }, modifier = Modifier.fillMaxWidth().height(100.dp), maxLines = 3)
            }
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(initialHour = time.hour, initialMinute = time.minute)
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    time = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("Cancel·lar") } },
            text = { TimePicker(state = timePickerState) }
        )
    }
}