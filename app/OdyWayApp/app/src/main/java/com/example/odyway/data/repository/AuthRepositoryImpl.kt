package com.example.odyway.data.repository

import com.example.odyway.data.local.SettingsManager
import com.example.odyway.domain.AuthRepository
import com.example.odyway.domain.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val settingsManager: SettingsManager
) : AuthRepository {

    // Fingimos el estado de sesión mirando si hay un "username" guardado
    // (En la versión final de Firebase, esto mirará el FirebaseAuth.instance.currentUser)
    override val currentUser: Flow<User?> = settingsManager.usernameFlow.map { username ->
        if (username.isNotBlank() && username != "Invitado") {
            User(
                id = username, // Usamos el nombre como ID temporal
                name = username,
                username = username,
                email = "$username@odyway.com",
                profileImageUrl = null
            )
        } else {
            null
        }
    }

    override suspend fun login(email: String, password: String): Result<Unit> {
        // Simulamos retardo de red
        delay(1000)

        // Validación súper básica de prueba
        if (email.isNotBlank() && password.length >= 6) {
            // "Logueamos" al usuario guardando su nombre en preferencias
            val fakeUsername = email.substringBefore("@").replaceFirstChar { it.uppercase() }
            settingsManager.username = fakeUsername
            return Result.success(Unit)
        }
        return Result.failure(Exception("Credenciales incorrectas. (Prueba con cualquier email y pass>5 chars)"))
    }

    override suspend fun register(email: String, password: String, username: String, fullName: String): Result<Unit> {
        delay(1000)
        if (email.isNotBlank() && password.length >= 6 && username.isNotBlank()) {
            // Autologueamos después de registrar
            settingsManager.username = username
            return Result.success(Unit)
        }
        return Result.failure(Exception("Revisa que los campos no estén vacíos y la contraseña sea segura."))
    }

    override suspend fun logout(): Result<Unit> {
        delay(500)
        // Borramos el usuario para "cerrar sesión"
        settingsManager.username = "Invitado"
        return Result.success(Unit)
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        delay(1000)
        if (email.isNotBlank()) {
            return Result.success(Unit)
        }
        return Result.failure(Exception("Introduce un email válido"))
    }
}