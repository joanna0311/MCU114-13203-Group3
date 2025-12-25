package com.example.dailychecktodo

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.dailychecktodo.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    // Interface to communicate with the MainActivity
    interface OnLoginSuccessListener {
        fun onLoginSuccess(email: String)
    }

    private var listener: OnLoginSuccessListener? = null

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnLoginSuccessListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnLoginSuccessListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences("user_auth", Context.MODE_PRIVATE)

        binding.buttonLogin.setOnClickListener {
            handleLogin()
        }

        binding.buttonRegister.setOnClickListener {
            handleRegister()
        }
    }

    private fun handleLogin() {
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val storedPassword = sharedPreferences.getString(email, null)

        if (storedPassword == password) {
            // Login success: Set login flag and notify the listener
            with(sharedPreferences.edit()) {
                putBoolean("is_logged_in", true)
                putString("current_user_email", email)
                apply()
            }
            Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()
            listener?.onLoginSuccess(email)
        } else {
            Toast.makeText(requireContext(), "Invalid email or password", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleRegister() {
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        if (sharedPreferences.contains(email)) {
            Toast.makeText(requireContext(), "This email is already registered", Toast.LENGTH_SHORT).show()
        } else {
            sharedPreferences.edit().putString(email, password).apply()
            Toast.makeText(requireContext(), "Registration successful! You can now log in.", Toast.LENGTH_LONG).show()
            handleLogin()
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
