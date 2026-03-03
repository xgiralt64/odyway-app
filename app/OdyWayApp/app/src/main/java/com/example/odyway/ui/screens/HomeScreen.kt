package com.example.odyway.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.odyway.NavGraph
// Asegúrate de importar tu clase R para las imágenes
import com.example.odyway.R
import com.example.odyway.ui.theme.MapPinRed
import com.example.odyway.ui.theme.NavyBlue

// 1. Creamos un modelo de datos temporal para cumplir con la tarea de Mockeo.
// (Más adelante moveremos esta clase a tu carpeta "domain/")
data class TripMock(
    val id: Int,
    val title: String,
    val destination: String,
    val date: String
)

// Datos
val misViajesMock = listOf(
    TripMock(1, "Asadito de fin de semana", "París, Francia", "12 Oct - 15 Oct"),
    TripMock(2, "Aventura Asiática", "Kioto, Japón", "01 Nov - 15 Nov"),
    TripMock(3, "Ruta en Coche al buffet", "Costa Brava, España", "20 Ago - 25 Ago"),
    TripMock(4, "Viaje de Negocios", "Londres, UK", "05 Sep - 08 Sep"),
    TripMock(5, "Skiada en la nieve", "Andorra", "10 Dic - 20 Dic")
)


@Composable
fun HomeScreen() {

    Scaffold(
        topBar = {
            // Creamos nuestra propia barra superior con un Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavyBlue) // Aquí aplicamos tu color NavyBlue
                    // Padding para darle un buen tamaño a la barra
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween // Separa el logo/título de la lupa
            ) {

                // Lado izquierdo: Logo y Título
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
                        color = Color.White,
                        fontSize = 30.sp

                    )
                }

                // Lado derecho: Icono de Lupa
                IconButton(onClick = { /* TODO: Acción de búsqueda */ }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar viaje",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Acción para el botón + */ },
                containerColor = MapPinRed,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "add"
                )
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

            items(misViajesMock) { trip ->
                TripCard(trip)
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun TripCard(trip: TripMock) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "📍 ${trip.destination}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "📅 ${trip.date}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Preview
@Composable
fun Preview() {
    HomeScreen()
}