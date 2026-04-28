package com.example.odyway.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.example.odyway.domain.AuthRepository
import com.example.odyway.domain.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth // Hilt inyecta Firebase automáticamente
) : AuthRepository {

    // MAGIA REACTIVA: Creamos un flujo que escucha a Firebase.
    // Si la sesión caduca o el usuario cierra sesión, esto avisa a toda la app al instante.
    override val currentUser: Flow<User?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                // Transformamos el usuario de Firebase a nuestro modelo de Dominio
                val user = User(
                    id = firebaseUser.uid, // El ID único e irrompible de Firebase
                    name = firebaseUser.displayName ?: "Viajero",
                    username = firebaseUser.displayName ?: "viajero",
                    email = firebaseUser.email ?: "",
                    profileImageUrl = firebaseUser.photoUrl?.toString()
                )
                trySend(user)
            } else {
                trySend(null)
            }
        }

        firebaseAuth.addAuthStateListener(authStateListener)
        awaitClose { firebaseAuth.removeAuthStateListener(authStateListener) }
    }

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            // El .await() espera a que Firebase termine la operación por red
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            // Firebase devuelve mensajes útiles (ej. "Contraseña incorrecta", "Usuario no existe")
            Result.failure(Exception(e.localizedMessage ?: "Error al iniciar sesión"))
        }
    }

    override suspend fun register(email: String, password: String, username: String, fullName: String): Result<Unit> {
        return try {
            // 1. Creamos la cuenta en Firebase
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            // 2. Firebase Auth solo tiene un campo básico para el nombre ("displayName").
            // Le guardamos ahí el nombre de usuario para poder mostrarlo luego.
            val user = authResult.user
            if (user != null) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build()
                user.updateProfile(profileUpdates).await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Error al registrarse"))
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            firebaseAuth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Error al cerrar sesión"))
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            // Manda el email de recuperación automáticamente
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Error al enviar el correo"))
        }
    }

    // --- NUEVAS FUNCIONES DE VERIFICACIÓN (T3.2) ---
    override suspend fun sendEmailVerification(): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                user.sendEmailVerification().await()
                Log.i(TAG, "Email de verificación enviado a ${user.email}")
                Result.success(Unit)
            } else {
                Result.failure(Exception("No hay usuario logueado"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error enviando email de verificación: ${e.message}", e)
            Result.failure(Exception(e.localizedMessage ?: "Error enviando verificación"))
        }
    }

    override fun isEmailVerified(): Boolean {
        return firebaseAuth.currentUser?.isEmailVerified ?: false
    }
}