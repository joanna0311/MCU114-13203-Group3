package com.example.dailychecktodo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailychecktodo.databinding.FragmentContactsBinding

class ContactsFragment : Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 建立範例聯絡人資料
        val contacts = listOf(
            Contact("王小明"),
            Contact("張小美"),
            Contact("陳小春"),
            Contact("王小真")
        )

        // 設定 RecyclerView
        binding.recyclerViewContacts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewContacts.adapter = ContactAdapter(contacts)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
