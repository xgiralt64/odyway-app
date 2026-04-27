package com.example.odyway.domain

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    // Exponemos el usuario actual. Será null si no hay nadie logueado.
    val currentUser: Flow<User?>

    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun register(email: String, password: String, username: String, fullName: String): Result<Unit>
    suspend fun logout(): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
}