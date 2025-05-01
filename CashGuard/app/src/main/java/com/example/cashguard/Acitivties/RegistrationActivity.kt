package com.example.cashguard.Acitivties

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.cashguard.data.User
import com.example.cashguard.ViewModel.UserViewModel
import kotlinx.coroutines.launch
import com.example.cashguard.databinding.ActivityRegistrationBinding
import com.example.cashguard.Intent.loginIntent
import com.example.cashguard.Intent.registerIntent
import com.example.cashguard.ViewModel.CategoryViewModel

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //go to registration page
        binding.redirectRegisterToLoginButton.setOnClickListener{
            registerIntent(this, LoginActivity::class.java)
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

            val categoryViewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)


            lifecycleScope.launch {
                val isEmailExists = userViewModel.isEmailRegistered(email)
                if (isEmailExists) {
                    Toast.makeText(this@RegistrationActivity, "Email already exists", Toast.LENGTH_SHORT).show()
                } else {
                    userViewModel.insertUser(user)

                    val userId = userViewModel.getUserIdByEmail(user.email)
                    categoryViewModel.createDefaultCategories(userId ?:0)

                    Toast.makeText(this@RegistrationActivity, "Registration successful", Toast.LENGTH_SHORT).show()
                    loginIntent(this@RegistrationActivity, LoginActivity::class.java)
                }
            }
        }
    }
}