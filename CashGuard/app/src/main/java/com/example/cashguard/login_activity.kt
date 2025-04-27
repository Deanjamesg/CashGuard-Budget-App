package com.example.cashguard

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cashguard.databinding.ActivityLoginBinding
import com.example.cashguard.databinding.ActivityMainBinding

class login_activity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //go to registration page
        binding.redirectLoginToRegisterButton.setOnClickListener{
            registerIntent(this, registration_activity::class.java)
        }

    }
}