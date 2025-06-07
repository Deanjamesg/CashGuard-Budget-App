package com.example.cashguard.Repository

import com.example.cashguard.Dao.UserDao
import com.example.cashguard.data.User

class UserRepository(private val userDao: UserDao) {
    suspend fun insertUser(user: User) = userDao.insert(user)
    suspend fun getUserByEmail(email: String) = userDao.getUserByEmail(email)
    suspend fun getUserIdByEmail(email: String) = userDao.getUserIdByEmail(email)
    suspend fun validateUser(email: String, password: String): User? {
        return userDao.getUserByCredentials(email, password)
    }

    suspend fun insertUserAndGetId(firstName: String, lastName: String, email: String, password: String): Int {

        val user = User(
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = password
        )
        return userDao.insertAndGetId(user).toInt()
    }
}