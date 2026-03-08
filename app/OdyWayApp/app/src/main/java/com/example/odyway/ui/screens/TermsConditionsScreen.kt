package com.example.odyway.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.odyway.ui.theme.OdyWayTheme

@Composable
fun TermsConditionsScreen(onNavigateBack: () -> Unit) {
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
                        Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Terms & Conditions",
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título principal
            Text(
                text = "Terms And Conditions",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                // Hacemos que solo el texto legal tenga scroll
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Last updated: March 2026\n\n" +
                                "1. Acceptance of the Terms\n" +
                                "By accessing and using the OdyWay application, you agree to be bound by these terms and conditions. If you do not agree with any part of the terms, you may not use the service.\n\n" +
                                "2. Use of the Service\n" +
                                "OdyWay is a tool for planning trips. We reserve the right to modify or suspend the service at any time without prior notice.\n\n" +
                                "3. Privacy and Data\n" +
                                "Your privacy is important to us. We collect and use personal information in accordance with our Privacy Policy.\n\n" +
                                "4. Intellectual Property\n" +
                                "All content, design, logos, and code of the application are the exclusive property of the OdyWay developers.\n\n" +
                                "5. Limitation of Liability\n" +
                                "We are not responsible for any losses, damages, or issues that may arise during your trips planned with our application.\n\n" +
                                "6. Modifications\n" +
                                "We reserve the right to revise these terms at any time. By continuing to use the application after such revisions, you agree to the updated terms.",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Justify
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botones Aceptar / Rechazar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // btn rechazar
                Button(
                    onClick = onNavigateBack, // Vuelve atrás como acción mock
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Reject", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(16.dp))

                // btn aceptar
                Button(
                    onClick = onNavigateBack, // Vuelve atrás como acción mock
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Accept", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Terms Mode Light")
@Composable
fun TermsConditionsScreenPreviewLight() {
    OdyWayTheme(darkTheme = false) {
        TermsConditionsScreen(onNavigateBack = {})
    }
}

@Preview(showBackground = true, name = "Terms Mode Dark")
@Composable
fun TermsConditionsScreenPreviewDark() {
    OdyWayTheme(darkTheme = true) {
        TermsConditionsScreen(onNavigateBack = {})
    }
}