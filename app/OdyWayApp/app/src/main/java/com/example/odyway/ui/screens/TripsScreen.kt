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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.odyway.domain.Image
import com.example.odyway.ui.theme.NavyBlue
import com.example.odyway.ui.theme.CyanBlue
import com.example.odyway.ui.theme.BackgroundLight
import com.example.odyway.ui.theme.GoldOrange
import com.example.odyway.ui.theme.White
import com.example.odyway.ui.theme.MountainGreen
import com.example.odyway.ui.theme.MapPinRed
import com.example.odyway.R

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

val itinerarioMock = listOf(
    ActivityMock(1, "09:00", "Cafe i esmorzar tradicional", 12.50),
    ActivityMock(2, "10:30", "Visita guiada pel centre historic", 25.00),
    ActivityMock(3, "14:00", "Dinar a un restaurant local", 35.00),
    ActivityMock(4, "17:00", "Passeig lliure i compres", 0.00),
    ActivityMock(5, "20:00", "Sopar amb vistes a la ciutat", 45.00)
)

@Composable
fun TripsScreen() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Itinerary", "Galery", "Costs")

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            Column {
                // --- NUEVO: Imagen superior con título y fechas ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp) // Altura de la imagen de cabecera
                ) {
                    // Imagen del viaje
                    Image(
                        painter = painterResource(id = R.drawable.paris_example),
                        contentDescription = "Imatge de París",
                        contentScale = ContentScale.Crop, // Llena el Box recortando si es necesario
                        modifier = Modifier.fillMaxSize()
                    )

                    // Gradiente oscuro para que el texto sea legible
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, NavyBlue.copy(alpha = 0.9f)),
                                    startY = 150f
                                )
                            )
                    )

                    // Título y fechas superpuestos en la esquina inferior izquierda
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Viatge a París",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Icono de calendario
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Dates",
                                tint = White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // Fechas de ida y vuelta
                            Text(
                                text = "12 Oct - 15 Oct",
                                style = MaterialTheme.typography.bodyMedium,
                                color = White
                            )
                        }
                    }
                }
                // --- hasta qui colocamos la imagen   ---

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = NavyBlue,
                    contentColor = White,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = GoldOrange
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title, color = if (selectedTabIndex == index) GoldOrange else White) }
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
                1 -> Text("Aquí anirà la Galeria d'imatges", modifier = Modifier.padding(16.dp), color = NavyBlue)
                2 -> Text("Estadístiques detallades de despeses", modifier = Modifier.padding(16.dp), color = NavyBlue)
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
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatItem("Budget", "1000€", NavyBlue)
            StatItem("Wasted", "117.50€", MapPinRed)
            StatItem("Remaining", "882.50€", MountainGreen)
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
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
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Schedule, contentDescription = "Hora", tint = MapPinRed, modifier = Modifier.size(20.dp))
                Text(text = activity.time, fontWeight = FontWeight.Bold, color = NavyBlue)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = activity.title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                color = NavyBlue
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Cost", tint = MountainGreen, modifier = Modifier.size(16.dp))
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
@Preview
@Composable
fun Preview() {
    TripsScreen()
}