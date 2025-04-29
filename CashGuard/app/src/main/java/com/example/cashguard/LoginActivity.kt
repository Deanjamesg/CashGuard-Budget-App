package com.example.cashguard

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.cashguard.Model.UserViewModel
import com.example.cashguard.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //go to registration page
        binding.redirectLoginToRegisterButton.setOnClickListener{
            registerIntent(this, RegistrationActivity::class.java)
        }

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val user = userViewModel.validateUserCredentials(email, password)
                if (user != null) {
                    // Show welcome toast
                    runOnUiThread {
                        Toast.makeText(
                            this@LoginActivity,
                            "Welcome ${user.firstName}!",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    overViewIntent(
                        this@LoginActivity,
                        BudgetOverviewActivity::class.java,
                        user.firstName,
                        user.userId
                    )
                    finish()
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this@LoginActivity,
                            "Invalid email or password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}