package com.example.cashguard

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.cashguard.data.User
import com.example.cashguard.Model.UserViewModel
import kotlinx.coroutines.launch

import com.example.cashguard.databinding.ActivityRegistrationBinding

class registration_activity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //go to registration page
        binding.redirectRegisterToLoginButton.setOnClickListener{
            registerIntent(this, login_activity::class.java)
        }

        // Add registration button click listener
        binding.registerButton.setOnClickListener {
            val firstName = binding.firstNameEditText.text.toString()
            val lastName = binding.lastNameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()

            // Validate inputs
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = User(
                firstName = firstName,
                lastName = lastName,
                email = email,
                password = password
            )

            val userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

            lifecycleScope.launch {
                val isEmailExists = userViewModel.isEmailRegistered(email)
                if (isEmailExists) {
                    Toast.makeText(this@registration_activity, "Email already exists", Toast.LENGTH_SHORT).show()
                } else {
                    userViewModel.insertUser(user)
                    Toast.makeText(this@registration_activity, "Registration successful", Toast.LENGTH_SHORT).show()
                    loginIntent(this@registration_activity, login_activity::class.java)
                }
            }
        }
    }
}