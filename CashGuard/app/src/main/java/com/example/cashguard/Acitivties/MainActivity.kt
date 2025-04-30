package com.example.cashguard.Acitivties

import android.os.Bundle

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.cashguard.databinding.ActivityMainBinding
import com.example.cashguard.Intent.loginIntent
import com.example.cashguard.Intent.registerIntent


class MainActivity : AppCompatActivity() {

    //creates the binding variable
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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