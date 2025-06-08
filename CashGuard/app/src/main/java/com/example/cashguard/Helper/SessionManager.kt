package com.example.cashguard.Helper

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun saveUserSession(userId: String) {
        editor.putString("USER_ID", userId)
        editor.apply()
    }

    fun getUserId(): String {
        return sharedPreferences.getString("USER_ID", "-1") ?: "-1"
    }

    fun signOut() {
        editor.remove("USER_ID")
        editor.apply()
    }

    fun clearSession() {
        editor.clear()
        editor.apply()
    }
}