package com.example.cashguard

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cashguard.databinding.ActivityRegistrationBinding

class registration_activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration)

        val binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.redirectRegisterToLoginButton.setOnClickListener{
            loginIntent(this, login_activity::class.java)
        }
    }
}