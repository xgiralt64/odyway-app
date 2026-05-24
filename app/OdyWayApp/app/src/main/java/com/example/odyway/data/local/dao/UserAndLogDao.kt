package com.example.odyway.data.local.dao

import androidx.room.*
import com.example.odyway.data.local.entity.AccessLogEntity
import com.example.odyway.data.local.entity.UserEntity

@Dao
interface UserAndLogDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Insert
    suspend fun insertAccessLog(log: AccessLogEntity)

    @Query("SELECT COUNT(*) FROM users WHERE username = :username COLLATE NOCASE")
    suspend fun countByUsername(username: String): Int
}