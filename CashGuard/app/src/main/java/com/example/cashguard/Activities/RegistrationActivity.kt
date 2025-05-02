package com.example.cashguard.Activities

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.cashguard.data.User
import com.example.cashguard.ViewModel.UserViewModel
import kotlinx.coroutines.launch
import com.example.cashguard.databinding.ActivityRegistrationBinding
import com.example.cashguard.Helper.loginIntent
import com.example.cashguard.ViewModel.CategoryViewModel

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding
    //private var userId : Int =-1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.redirectRegisterToLoginButton.setOnClickListener {
            loginIntent(this, LoginActivity::class.java)
        }

        // Register button click listener
        binding.registerButton.setOnClickListener {
            val firstName = binding.firstNameEditText.text.toString().trim()
            val lastName = binding.lastNameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()

            // Validation checks
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

            var userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
            var categoryViewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)

            lifecycleScope.launch {
                try {
                    // 1. Check if email exists
                    if (userViewModel.isEmailRegistered(email)) {
                        Toast.makeText(
                            this@RegistrationActivity,
                            "Email already exists",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }

                    // 2. Insert user
                    userViewModel.insertUser(user)

                    // 3. Get user ID as nullable Int
                    val userId: Int? = userViewModel.getUserIdByEmail(email)
                    Log.d("Registration", "User ID retrieved: $userId")

                    // 4. Check if ID is valid
                    if (userId == null || userId == -1) {
                        Toast.makeText(
                            this@RegistrationActivity,
                            "Registration failed - try again",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }

                    // 5. Initialize categories with valid user ID
                    categoryViewModel.initializeUserCategories(userId)
                    //categoryViewModel.createDefaultCategories(userId)

                    // 6. Navigate to login
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Registration successful!",
                        Toast.LENGTH_SHORT
                    ).show()
                    loginIntent(this@RegistrationActivity, LoginActivity::class.java)
                } catch (e: Exception) {
                    Log.e("Registration", "Error: ${e.message}", e)
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Registration failed: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}