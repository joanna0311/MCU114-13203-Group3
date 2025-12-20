package com.example.dailychecktodo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailychecktodo.databinding.FragmentContactsBinding

class ContactsFragment : Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    private lateinit var contactAdapter: ContactAdapter

    // Register the permission callback
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted. Load the contacts.
            loadContacts()
        } else {
            // Permission is denied. Show a message.
            Toast.makeText(requireContext(), "Permission denied. Cannot load contacts.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        checkAndLoadContacts()
    }

    private fun setupRecyclerView() {
        contactAdapter = ContactAdapter() // Initialize the new ListAdapter
        binding.recyclerViewContacts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewContacts.adapter = contactAdapter
    }

    private fun checkAndLoadContacts() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                loadContacts()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                // In an educational UI, explain to the user why you need the permission.
                // For now, we just request it.
                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
            else -> {
                // Directly ask for the permission.
                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
        }
    }

    private fun loadContacts() {
        val contacts = mutableListOf<Contact>()
        // Define the columns you want to retrieve
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)

        // Get the ContentResolver
        val contentResolver = requireActivity().contentResolver

        // Query the contacts
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null, // No selection criteria, retrieve all
            null, // No selection arguments
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC" // Sort by name
        )

        cursor?.use { // Use a 'use' block to ensure the cursor is closed
            val nameColumn = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            while (it.moveToNext()) {
                val name = it.getString(nameColumn)
                contacts.add(Contact(name))
            }
        }

        // Use submitList() to update the ListAdapter with the new list of contacts
        contactAdapter.submitList(contacts.distinctBy { it.name }) // Remove duplicates
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
