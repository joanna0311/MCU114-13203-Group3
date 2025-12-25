package com.example.dailychecktodo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailychecktodo.databinding.FragmentContactsBinding

class ContactsFragment : Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    private val habitViewModel: HabitViewModel by activityViewModels()
    private lateinit var contactAdapter: ContactAdapter

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            loadContacts()
        } else {
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
        contactAdapter = ContactAdapter { contact ->
            showHabitSelectionDialog(contact)
        }
        binding.recyclerViewContacts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewContacts.adapter = contactAdapter
    }

    private fun showHabitSelectionDialog(contact: Contact) {
        val completedHabits = habitViewModel.habits.value?.filter { it.isCompleted } ?: emptyList()

        if (completedHabits.isEmpty()) {
            Toast.makeText(requireContext(), "您還沒有已完成的習慣可以分享！", Toast.LENGTH_SHORT).show()
            return
        }

        val habitNames = completedHabits.map { it.name }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle("分享您的成就給 ${contact.name}")
            .setItems(habitNames) { _, which ->
                val selectedHabit = completedHabits[which]
                sendSms(contact.phoneNumber, "我剛剛完成了我的目標：『${selectedHabit.name}』！為我加油吧！")
            }
            .show()
    }

    private fun sendSms(phoneNumber: String, message: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:$phoneNumber")
            putExtra("sms_body", message)
        }
        startActivity(intent)
    }

    private fun checkAndLoadContacts() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                loadContacts()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
        }
    }

    private fun loadContacts() {
        val contacts = mutableListOf<Contact>()
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val contentResolver = requireActivity().contentResolver

        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null, 
            null, 
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        cursor?.use { 
            val nameColumn = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberColumn = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (it.moveToNext()) {
                val name = it.getString(nameColumn)
                val number = it.getString(numberColumn)
                contacts.add(Contact(name, number))
            }
        }

        contactAdapter.submitList(contacts.distinctBy { it.phoneNumber })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
