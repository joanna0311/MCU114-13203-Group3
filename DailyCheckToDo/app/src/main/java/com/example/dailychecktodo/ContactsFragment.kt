package com.example.dailychecktodo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
// import com.example.dailychecktodo.databinding.FragmentContactsBinding // 如果您有對應的 layout

class ContactsFragment : Fragment() {

    // private var _binding: FragmentContactsBinding? = null
    // private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // _binding = FragmentContactsBinding.inflate(inflater, container, false)
        // return binding.root
        // 如果您還沒有為這個 Fragment 建立 XML 佈局，可以先回傳一個簡單的 TextView
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 在這裡撰寫聯絡人列表的邏輯
    }

    // override fun onDestroyView() {
    //     super.onDestroyView()
    //     _binding = null
    // }
}
