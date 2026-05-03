package com.example.odyway.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val username: String,
    val email: String,
    val profileImageUrl: String?,
    val birthDate: Long,
    val login: String = email,
    val address: String = "",
    val country: String = "",
    val phone: String = "",
    val acceptEmails: Boolean = false
)