package com.example.odyway.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.odyway.R

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Logo
        Image(
            painter = painterResource(id = R.drawable.icon_odyway),
            contentDescription = "Logo",
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 32.dp)
        )

        // Camp usuari
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuari") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Camp contrasenya
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contrasenya") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botó iniciar sessió
        Button(
            onClick = {
                // De moment sense comprovar res
                onLoginSuccess()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sessió")
        }
    }
}