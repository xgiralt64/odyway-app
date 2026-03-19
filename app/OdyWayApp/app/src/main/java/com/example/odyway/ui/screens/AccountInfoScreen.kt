package com.example.odyway.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.odyway.R
import com.example.odyway.data.local.SettingsManager
import com.example.odyway.ui.theme.OdyWayTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountInfoScreen(settingsManager: SettingsManager, onNavigateBack: () -> Unit) {
    val savedUsername by settingsManager.usernameFlow.collectAsState()
    val savedBirthDate by settingsManager.birthDateFlow.collectAsState()

    var username by remember(savedUsername) { mutableStateOf(savedUsername) }
    var birthDateMillis by remember(savedBirthDate) { mutableStateOf(savedBirthDate) }
    
    var email by remember { mutableStateOf("xavier@xgiralt.com") }
    var fullName by remember { mutableStateOf("Xavier Giralt") }
    var bio by remember { mutableStateOf("Passionat pels viatges i la tecnologia.") }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = if (birthDateMillis > 0) birthDateMillis else System.currentTimeMillis()
    )

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val birthDateDisplay = if (birthDateMillis > 0) dateFormatter.format(Date(birthDateMillis)) else ""

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    birthDateMillis = datePickerState.selectedDateMillis ?: birthDateMillis
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back_button),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.account_info_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            // Secció dades personals
            Text(
                text = stringResource(id = R.string.account_info_section_personal),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp, start = 4.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text(stringResource(id = R.string.account_info_full_name)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(stringResource(id = R.string.account_info_email)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Birth Date Field (Using a Box to handle click properly with Material 3)
                    Box(modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }) {
                        OutlinedTextField(
                            value = birthDateDisplay,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text(stringResource(id = R.string.account_info_birth_date)) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false, 
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Secció perfil públic
            Text(
                text = stringResource(id = R.string.account_info_section_public),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp, start = 4.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text(stringResource(id = R.string.account_info_username)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        prefix = { Text("@") }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text(stringResource(id = R.string.account_info_bio)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 3,
                        leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    settingsManager.username = username
                    settingsManager.birthDate = birthDateMillis
                    onNavigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = stringResource(id = R.string.save_button),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Account Info Light")
@Composable
fun AccountInfoScreenPreviewLight() {
    val context = LocalContext.current
    OdyWayTheme(darkTheme = false) {
        AccountInfoScreen(settingsManager = SettingsManager(context), onNavigateBack = {})
    }
}

@Preview(showBackground = true, name = "Account Info Dark")
@Composable
fun AccountInfoScreenPreviewDark() {
    val context = LocalContext.current
    OdyWayTheme(darkTheme = true) {
        AccountInfoScreen(settingsManager = SettingsManager(context), onNavigateBack = {})
    }
}
