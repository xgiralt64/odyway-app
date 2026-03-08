package com.example.odyway.domain

data class User(
    val id: String,
    val name: String,
    val username: String,
    val email: String,
    val profileImageUrl: String?
)