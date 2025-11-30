package com.example.dailychecktodo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.dailychecktodo.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 設定性別下拉選單的選項
        val genderOptions = resources.getStringArray(R.array.gender_options)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, genderOptions)
        binding.autoCompleteGender.setAdapter(adapter)

        // 在這裡處理儲存按鈕的點擊事件等邏輯
        binding.buttonSaveSettings.setOnClickListener {
            // 在這裡可以加入實際儲存使用者設定的邏輯
            
            // 顯示「已儲存」訊息
            Toast.makeText(requireContext(), "已儲存", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
