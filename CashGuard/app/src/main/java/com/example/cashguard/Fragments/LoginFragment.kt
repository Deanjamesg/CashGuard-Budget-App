package com.example.cashguard.Fragments

import android.content.Intent
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.cashguard.Activities.UserActivity
import com.example.cashguard.Helper.SessionManager
import com.example.cashguard.R
import com.example.cashguard.ViewModel.LoginViewModel
import com.example.cashguard.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // DELETE FROM HERE
        val sessionManager = SessionManager(requireContext())
        Log.d("Test", "LoginFragment User ID: ${sessionManager.getUserId()}")
        // TO HERE

        binding.submitLoginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            viewModel.onLoginClicked(email, password)
        }

        binding.textViewGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.onToastMessageShown()
            }
        }

        viewModel.signedIn.observe(viewLifecycleOwner) { signedIn ->
            signedIn?.let {
                if (it) {
                    val intent = Intent(requireActivity(), UserActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}