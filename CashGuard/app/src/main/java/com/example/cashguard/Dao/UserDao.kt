package com.example.cashguard.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.cashguard.data.User

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Insert
    suspend fun insertAndGetId(user: User): Long

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT userId FROM users WHERE email = :email")
    suspend fun getUserIdByEmail(email: String): Int?

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun getUserByCredentials(email: String, password: String): User?
}