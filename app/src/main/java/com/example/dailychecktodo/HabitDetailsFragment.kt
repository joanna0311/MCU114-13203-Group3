package com.example.dailychecktodo

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.dailychecktodo.databinding.FragmentHabitDetailsBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HabitDetailsFragment : Fragment() {

    private var _binding: FragmentHabitDetailsBinding? = null
    private val binding get() = _binding!!

    private val habitViewModel: HabitViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPriorityMenu()

        habitViewModel.editingHabit.observe(viewLifecycleOwner) { habit ->
            habit?.let {
                binding.tilDate.editText?.setText(it.date)
                binding.tilTime.editText?.setText(it.time)
                if (binding.actvPriority.text.toString() != it.priority) {
                    binding.actvPriority.setText(it.priority, false)
                }
            }
        }

        binding.tilDate.editText?.setOnClickListener {
            showDatePicker()
        }

        binding.tilTime.editText?.setOnClickListener {
            showTimePicker()
        }

        binding.actvPriority.setOnItemClickListener { parent, _, position, _ ->
            val selectedPriority = parent.adapter.getItem(position) as String
            habitViewModel.editingHabit.value?.let {
                if (it.priority != selectedPriority) {
                    habitViewModel.updateEditingHabit(it.copy(priority = selectedPriority))
                }
            }
        }

        binding.buttonSaveDetails.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setupPriorityMenu() {
        val priorities = listOf("高", "中", "低")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, priorities)
        binding.actvPriority.setAdapter(adapter)
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView(calendar)
        }
        DatePickerDialog(
            requireContext(),
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val timeText = binding.tilTime.editText?.text.toString()
        if (timeText.isNotEmpty()) {
            val sdf = SimpleDateFormat("HH:mm", Locale.US)
            try {
                val date = sdf.parse(timeText)
                if (date != null) {
                    calendar.time = date
                }
            } catch (e: java.text.ParseException) {
                // Keep calendar as current time if parsing fails
            }
        }

        val picker = MaterialTimePicker.Builder()
            .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(calendar.get(Calendar.HOUR_OF_DAY))
            .setMinute(calendar.get(Calendar.MINUTE))
            .setTitleText("Set time")
            .build()

        picker.addOnPositiveButtonClickListener {
            val finalCalendar = Calendar.getInstance()

            val dateText = binding.tilDate.editText?.text.toString()
            if (dateText.isNotEmpty()) {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                try {
                    val parsedDate = sdf.parse(dateText)
                    if (parsedDate != null) {
                        finalCalendar.time = parsedDate
                    }
                } catch (e: java.text.ParseException) {
                    // If parsing fails, it will just use today's date from getInstance()
                }
            }

            finalCalendar.set(Calendar.HOUR_OF_DAY, picker.hour)
            finalCalendar.set(Calendar.MINUTE, picker.minute)
            finalCalendar.set(Calendar.SECOND, 0)
            finalCalendar.set(Calendar.MILLISECOND, 0)

            updateTimeInView(finalCalendar)
        }

        picker.show(childFragmentManager, "time_picker")
    }

    private fun updateDateInView(calendar: Calendar) {
        val format = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(format, Locale.US)
        val dateString = sdf.format(calendar.time)
        binding.tilDate.editText?.setText(dateString)
        habitViewModel.editingHabit.value?.let {
            habitViewModel.updateEditingHabit(it.copy(date = dateString))
        }
    }

    private fun updateTimeInView(calendar: Calendar) {
        val format = "HH:mm"
        val sdf = SimpleDateFormat(format, Locale.US)
        val timeString = sdf.format(calendar.time)
        binding.tilTime.editText?.setText(timeString)
        habitViewModel.editingHabit.value?.let {
            habitViewModel.updateEditingHabit(it.copy(time = timeString))
        }
        scheduleReminder(calendar)
    }

    private fun scheduleReminder(calendar: Calendar) {
        val context = requireContext().applicationContext
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).also {
                it.data = Uri.fromParts("package", requireActivity().packageName, null)
                startActivity(it)
            }
            return
        }

        val finalCalendar = calendar.clone() as Calendar

        if (finalCalendar.timeInMillis <= System.currentTimeMillis()) {
            finalCalendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val habit = habitViewModel.editingHabit.value ?: return
        val habitId = habit.id.hashCode()

        val intent = Intent(context, HabitReminderReceiver::class.java).apply {
            putExtra("HABIT_NAME", habit.name)
            putExtra("HABIT_ID", habitId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habitId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            finalCalendar.timeInMillis,
            pendingIntent
        )

        Toast.makeText(context, "Reminder set for ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(finalCalendar.time)}", Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
