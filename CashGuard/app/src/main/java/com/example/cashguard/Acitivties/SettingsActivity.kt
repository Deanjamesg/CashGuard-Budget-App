package com.example.cashguard.Acitivties

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.cashguard.Intent.SessionManager
import com.example.cashguard.R

class SettingsActivity : AppCompatActivity() {

    private lateinit var sessionManager : SessionManager
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sessionManager = SessionManager(this)
        userId = sessionManager.getUserId()
        Log.d("SESSION", "Settings ID: ${sessionManager.getUserId()}")

    }
}