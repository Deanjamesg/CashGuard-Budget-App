package com.example.cashguard.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cashguard.data.User

@Dao
interface UserDao {

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT userId FROM users WHERE email = :email")
    suspend fun getUserIdByEmail(email: String): String

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun getUserByCredentials(email: String, password: String): User?
}