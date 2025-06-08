package com.example.cashguard.Fragments

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.cashguard.R
import com.example.cashguard.ViewModel.RegisterViewModel
import com.example.cashguard.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.onToastMessageShown()
            }
        }

        viewModel.registerState.observe(viewLifecycleOwner) { isRegistered ->
            isRegistered?.let {
                if (it) {
                    findNavController().navigate(R.id.action_register_to_login)
                }
            }
        }

        binding.submitRegisterButton.setOnClickListener {
            val firstNameEditText = binding.firstNameEditText.text.toString()
            val lastNameEditText = binding.lastNameEditText.text.toString()
            val emailEditText = binding.emailEditText.text.toString()
            val passwordEditText = binding.passwordEditText.text.toString()
            val confirmPasswordEditText = binding.confirmPasswordEditText.text.toString()

            viewModel.onRegisterClicked(
                firstName = firstNameEditText,
                lastName = lastNameEditText,
                email = emailEditText,
                password = passwordEditText,
                confirmPassword = confirmPasswordEditText
            )
        }

        binding.textViewGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_register_to_login)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}