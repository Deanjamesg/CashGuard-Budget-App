package com.example.cashguard.Repository

import com.example.cashguard.Dao.UserDao
import com.example.cashguard.data.User
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class UserRepository(private val userDao: UserDao) {

    // Get a reference to the 'users' node in your Firebase Realtime Database
    private val userDatabaseReference = FirebaseDatabase.getInstance().getReference("users")

    suspend fun insertUser(user: User) = userDao.insert(user)
    suspend fun getUserByEmail(email: String) = userDao.getUserByEmail(email)
    suspend fun getUserIdByEmail(email: String) = userDao.getUserIdByEmail(email)
    suspend fun validateUser(email: String, password: String): User? {
        return userDao.getUserByCredentials(email, password)
    }

    suspend fun insertUserAndGetId(firstName: String, lastName: String, email: String, password: String): String {

        val firebaseKey = userDatabaseReference.push().key
            ?: throw Exception("Could not generate a unique key from Firebase.")

        val user = User(
            userId = firebaseKey,
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = password
        )

        userDatabaseReference.child(firebaseKey).setValue(user).await()
        userDao.insert(user)

        return firebaseKey
    }
}