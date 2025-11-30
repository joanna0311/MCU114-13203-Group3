package com.example.dailychecktodo

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.dailychecktodo.databinding.FragmentHabitDetailsBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HabitDetailsFragment : Fragment() {

    private var _binding: FragmentHabitDetailsBinding? = null
    private val binding get() = _binding!!

    private val habitViewModel: HabitViewModel by activityViewModels()
    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()

        habitViewModel.editingHabit.observe(viewLifecycleOwner) { habit ->
            habit?.let {
                // 當 ViewModel 中的資料變化時，更新 UI
                if (binding.tilDate.editText?.text.toString() != it.date) {
                    binding.tilDate.editText?.setText(it.date)
                }
                if (binding.tilTime.editText?.text.toString() != it.time) {
                    binding.tilTime.editText?.setText(it.time)
                }
                if (binding.autoCompletePriority.text.toString() != it.priority) {
                    binding.autoCompletePriority.setText(it.priority, false)
                }
                if (binding.switchIsPublic.isChecked != it.isPublic) {
                    binding.switchIsPublic.isChecked = it.isPublic
                }
            }
        }

        binding.buttonSaveDetails.setOnClickListener {
            habitViewModel.editingHabit.value?.let { currentHabit ->
                val updatedHabit = currentHabit.copy(
                    date = binding.tilDate.editText?.text.toString(),
                    time = binding.tilTime.editText?.text.toString(),
                    priority = binding.autoCompletePriority.text.toString(),
                    isPublic = binding.switchIsPublic.isChecked
                )
                habitViewModel.updateEditingHabit(updatedHabit)
            }
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setupViews() {
        // 設定重要性下拉選單的選項
        val priorityOptions = resources.getStringArray(R.array.priority_options)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, priorityOptions)
        binding.autoCompletePriority.setAdapter(adapter)

        // 設定日期和時間選擇器
        setupDatepicker()
        setupTimePicker()
    }

    private fun setupDatepicker() {
        binding.tilDate.editText?.setOnClickListener { 
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }

            DatePickerDialog(
                requireContext(),
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupTimePicker() {
        binding.tilTime.editText?.setOnClickListener { 
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                updateTimeInView()
            }

            TimePickerDialog(
                requireContext(),
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true // 24小時制
            ).show()
        }
    }

    private fun updateDateInView() {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.TAIWAN)
        binding.tilDate.editText?.setText(sdf.format(calendar.time))
    }

    private fun updateTimeInView() {
        val myFormat = "HH:mm"
        val sdf = SimpleDateFormat(myFormat, Locale.TAIWAN)
        binding.tilTime.editText?.setText(sdf.format(calendar.time))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}