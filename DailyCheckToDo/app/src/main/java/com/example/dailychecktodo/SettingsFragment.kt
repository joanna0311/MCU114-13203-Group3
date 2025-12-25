package com.example.dailychecktodo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.dailychecktodo.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private var currentUserEmail: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences("user_auth", Context.MODE_PRIVATE)
        currentUserEmail = sharedPreferences.getString("current_user_email", null)

        if (currentUserEmail != null) {
            setupGenderDropdown()
            loadSettings()
        } else {
            // Handle case where user is not logged in, though this fragment shouldn't be accessible.
        }

        binding.buttonSaveSettings.setOnClickListener {
            saveSettings()
            Toast.makeText(requireContext(), "已儲存", Toast.LENGTH_SHORT).show()
        }

        binding.buttonLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("登出")
            .setMessage("您確定要登出嗎？")
            .setPositiveButton("是") { _, _ ->
                logout()
            }
            .setNegativeButton("否", null)
            .show()
    }

    private fun logout() {
        with(sharedPreferences.edit()) {
            putBoolean("is_logged_in", false)
            remove("current_user_email")
            apply()
        }

        val intent = Intent(requireActivity(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun setupGenderDropdown() {
        val genderOptions = try {
            resources.getStringArray(R.array.gender_options)
        } catch (e: Exception) {
            arrayOf("男性", "女性", "其他") // Fallback
        }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, genderOptions)
        binding.autoCompleteGender.setAdapter(adapter)
    }

    private fun loadSettings() {
        currentUserEmail?.let {
            binding.editTextName.setText(sharedPreferences.getString("${it}_name", ""))
            binding.editTextId.setText(sharedPreferences.getString("${it}_id", ""))
            binding.autoCompleteGender.setText(sharedPreferences.getString("${it}_gender", ""), false)
            binding.tilPhone.editText?.setText(sharedPreferences.getString("${it}_phone", ""))
            binding.tilEmail.editText?.setText(sharedPreferences.getString("${it}_email", ""))
            binding.tilBio.editText?.setText(sharedPreferences.getString("${it}_bio", ""))
        }
    }

    private fun saveSettings() {
        currentUserEmail?.let {
            with(sharedPreferences.edit()) {
                putString("${it}_name", binding.editTextName.text.toString())
                putString("${it}_id", binding.editTextId.text.toString())
                putString("${it}_gender", binding.autoCompleteGender.text.toString())
                putString("${it}_phone", binding.tilPhone.editText?.text.toString())
                putString("${it}_email", binding.tilEmail.editText?.text.toString())
                putString("${it}_bio", binding.tilBio.editText?.text.toString())
                apply()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
