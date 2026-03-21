package com.example.odyway.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.odyway.R
import com.example.odyway.domain.Trip
import com.example.odyway.ui.viewmodels.TripViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTripScreen(
    tripId: String? = null, // NUEVO: Si es null = Crear, Si tiene texto = Editar
    tripViewModel: TripViewModel,
    onNavigateBack: () -> Unit
) {
    // Buscamos si existe el viaje a editar
    val trips by tripViewModel.trips.collectAsState()
    val tripToEdit = trips.find { it.id == tripId }

    // Estados precargados si estamos editando, o vacíos si es nuevo
    var title by remember { mutableStateOf(tripToEdit?.title ?: "") }
    var destination by remember { mutableStateOf(tripToEdit?.destination ?: "") }
    var description by remember { mutableStateOf(tripToEdit?.description ?: "") }
    var budgetStr by remember { mutableStateOf(tripToEdit?.budget?.toString() ?: "") }
    var startDate by remember { mutableStateOf(tripToEdit?.startDate ?: LocalDate.now()) }
    var endDate by remember { mutableStateOf(tripToEdit?.endDate ?: LocalDate.now().plusDays(7)) }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    val isEditing = tripToEdit != null

    Scaffold(
        topBar = {
            TopAppBar(
                // Cambiamos el título dinámicamente
                title = { Text(if (isEditing) "Editar Viatge" else "Nou Viatge", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Tornar") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            BottomAppBar(containerColor = Color.Transparent, contentPadding = PaddingValues(16.dp)) {
                Button(
                    onClick = {
                        if (title.isBlank() || destination.isBlank() || budgetStr.isBlank()) {
                            showError = true
                        } else {
                            val newOrUpdatedTrip = Trip(
                                id = tripToEdit?.id ?: UUID.randomUUID().toString(), // Mantenemos ID si editamos
                                userId = "user-1",
                                title = title,
                                destination = destination,
                                description = description,
                                status = tripToEdit?.status ?: "Próxim",
                                startDate = startDate,
                                endDate = endDate,
                                budget = budgetStr.toDoubleOrNull() ?: 0.0
                            )

                            // Llamamos a actualizar o añadir según toque
                            if (isEditing) {
                                tripViewModel.updateTrip(newOrUpdatedTrip)
                            } else {
                                tripViewModel.addTrip(newOrUpdatedTrip)
                            }
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.save_button), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        // ... (Todo el contenido del Column con los OutlinedTextFields y DatePickers se queda exactamente igual que en el paso anterior)
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (showError) Text("Si us plau, omple els camps obligatoris.", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))

            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Títol del Viatge *") }, modifier = Modifier.fillMaxWidth(), singleLine = true, isError = showError && title.isBlank())
            OutlinedTextField(value = destination, onValueChange = { destination = it }, label = { Text("Destí *") }, modifier = Modifier.fillMaxWidth(), leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }, singleLine = true, isError = showError && destination.isBlank())

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedCard(onClick = { showStartDatePicker = true }, modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Data d'Inici", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Icon(Icons.Default.DateRange, tint = MaterialTheme.colorScheme.primary, contentDescription = null)
                        Text(startDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")), fontWeight = FontWeight.Bold)
                    }
                }
                OutlinedCard(onClick = { showEndDatePicker = true }, modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Data de Fi", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Icon(Icons.Default.Event, tint = MaterialTheme.colorScheme.error, contentDescription = null)
                        Text(endDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")), fontWeight = FontWeight.Bold)
                    }
                }
            }

            OutlinedTextField(value = budgetStr, onValueChange = { budgetStr = it }, label = { Text("Pressupost Total (€) *") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), leadingIcon = { Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }, singleLine = true, isError = showError && budgetStr.isBlank())
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripció (Opcional)") }, modifier = Modifier.fillMaxWidth().height(120.dp), maxLines = 4)
            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    // (Los bloques if (showStartDatePicker) y if (showEndDatePicker) se quedan igual)
    if (showStartDatePicker) {
        val startDatePickerState = rememberDatePickerState(initialSelectedDateMillis = startDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli())
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    startDatePickerState.selectedDateMillis?.let { millis ->
                        startDate = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                        if (startDate.isAfter(endDate)) endDate = startDate.plusDays(1)
                    }
                    showStartDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showStartDatePicker = false }) { Text("Cancel·lar") } }
        ) { DatePicker(state = startDatePickerState) }
    }

    if (showEndDatePicker) {
        val endDatePickerState = rememberDatePickerState(
            initialSelectedDateMillis = endDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val checkDate = Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneOffset.UTC).toLocalDate()
                    return !checkDate.isBefore(startDate)
                }
            }
        )
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    endDatePickerState.selectedDateMillis?.let { millis -> endDate = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate() }
                    showEndDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showEndDatePicker = false }) { Text("Cancel·lar") } }
        ) { DatePicker(state = endDatePickerState) }
    }
}