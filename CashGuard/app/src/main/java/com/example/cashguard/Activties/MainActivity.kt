package com.example.cashguard.Activties

import android.os.Bundle
import android.util.Log

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.cashguard.Intent.SessionManager
import com.example.cashguard.databinding.ActivityMainBinding
import com.example.cashguard.Intent.loginIntent
import com.example.cashguard.Intent.registerIntent

private lateinit var sessionManager : SessionManager
private var userId: Int = -1

class MainActivity : AppCompatActivity() {

    //creates the binding variable
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        userId = sessionManager.getUserId()
        Log.d("SESSION", "Main ID: ${sessionManager.getUserId()}")

        //go to login page
        binding.loginButton.setOnClickListener {
            loginIntent(this, LoginActivity::class.java)
        }

        //go to registration page
        binding.registerButton.setOnClickListener{
            registerIntent(this, RegistrationActivity::class.java)
        }
    }

}