package com.example.cashguard.Intent

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun saveUserSession(userId: Int) {
        editor.putInt("USER_ID", userId)
        editor.apply()
    }

    fun getUserId(): Int {
        return sharedPreferences.getInt("USER_ID", -1)
    }

    fun clearSession() {
        editor.clear()
        editor.apply()
    }
}