package com.example.odyway.data.repository

import android.util.Log
import com.example.odyway.data.local.dao.UserAndLogDao
import com.example.odyway.data.local.entity.AccessLogEntity
import com.example.odyway.data.local.entity.UserEntity
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
    private val firebaseAuth: FirebaseAuth,
    private val userAndLogDao: UserAndLogDao
) : AuthRepository {

    private companion object {
        const val TAG = "AuthRepository"
    }

    override val currentUser: Flow<User?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                val user = User(
                    id = firebaseUser.uid,
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
            Log.d(TAG, "Intentando iniciar sesión con: $email")
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user
            val userId = user?.uid ?: "unknown"

            // verfificacion de email comprobador
            if (user != null && !user.isEmailVerified) {
                Log.w(TAG, "Login bloqueado: Email no verificado para $email")
                firebaseAuth.signOut()
                return Result.failure(Exception("Por favor, verifica tu correo electrónico antes de iniciar sesión. Revisa tu bandeja de entrada."))
            }

            Log.i(TAG, "Inicio de sesión exitoso. UID: $userId")

            // Sincronizar el usuario de Firebase con Room
            if (user != null) {
                val userEntity = UserEntity(
                    id = user.uid,
                    name = user.displayName ?: "Viajero",
                    username = user.displayName ?: email.substringBefore("@"), // Usamos parte del email si no tiene nombre
                    email = user.email ?: email,
                    profileImageUrl = user.photoUrl?.toString(),
                    birthDate = 0L // Valor por defecto
                )
                // Insertamos el usuario. Si ya existe, OnConflictStrategy.REPLACE lo actualizará
                userAndLogDao.insertUser(userEntity)
                Log.i(TAG, "Usuario sincronizado correctamente en la base de datos local (Room).")
            }

            //guardar log de acceso login
            userAndLogDao.insertAccessLog(
                AccessLogEntity(userId = userId, timestamp = System.currentTimeMillis(), action = "LOGIN")
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error en login: ${e.message}", e)
            Result.failure(Exception(e.localizedMessage ?: "Error al iniciar sesión"))
        }
    }

    override suspend fun register(email: String, password: String, username: String, fullName: String): Result<Unit> {
        return try {
            Log.d(TAG, "Iniciando registro en Firebase para: $email")

            val existingUsers = userAndLogDao.countByUsername(username.trim())

            if (existingUsers > 0) {
                Log.w(TAG, "Registro bloqueado: El usuario '$username' ya existe")
                return Result.failure(Exception("El nombre de usuario '$username' ya está en uso. Por favor, elige otro."))
            }

            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Fallo al crear usuario en Firebase")

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build()
            firebaseUser.updateProfile(profileUpdates).await()
            Log.i(TAG, "Usuario creado en Firebase exitosamente. UID: ${firebaseUser.uid}")

            //guardar en nuestra Base de Datos Local (Room)
            val userEntity = UserEntity(
                id = firebaseUser.uid,
                name = fullName,
                username = username,
                email = email,
                profileImageUrl = null,
                birthDate = 0L
            )
            userAndLogDao.insertUser(userEntity)
            Log.i(TAG, "Usuario guardado en la base de datos local (Room).")

            //Enviar email de verificacion
            sendEmailVerification()

            // Forzamos el cierre de sesion
            firebaseAuth.signOut()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al registrarse: ${e.message}", e)
            Result.failure(Exception(e.localizedMessage ?: "Error al registrarse"))
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            val userId = firebaseAuth.currentUser?.uid
            Log.d(TAG, "Cerrando sesión para UID: $userId")

            if (userId != null) {
                // guardar log de acceso logout
                userAndLogDao.insertAccessLog(
                    AccessLogEntity(userId = userId, timestamp = System.currentTimeMillis(), action = "LOGOUT")
                )
            }

            firebaseAuth.signOut()
            Log.i(TAG, "Sesión cerrada correctamente")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al cerrar sesión: ${e.message}", e)
            Result.failure(Exception(e.localizedMessage ?: "Error al cerrar sesión"))
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            Log.d(TAG, "Enviando correo de recuperación a: $email")
            firebaseAuth.sendPasswordResetEmail(email).await()
            Log.i(TAG, "Correo de recuperación enviado con éxito")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error enviando correo de recuperación: ${e.message}", e)
            Result.failure(Exception(e.localizedMessage ?: "Error al enviar el correo"))
        }
    }

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