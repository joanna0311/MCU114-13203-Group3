package com.example.dailychecktodo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailychecktodo.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val habitViewModel: HabitViewModel by activityViewModels()
    private lateinit var habitAdapter: HabitAdapter
    private var selectedDate: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHabitsRecyclerView()
        setupCalendarView()

        binding.btnAddHabit.setOnClickListener {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val dateString = dateFormat.format(selectedDate.time)
            habitViewModel.startNewHabit(dateString)
            navigateToEditScreen()
        }

        habitViewModel.habits.observe(viewLifecycleOwner) { habits ->
            filterHabitsForSelectedDate()
        }
        
        // Initial filter for today
        filterHabitsForSelectedDate()
    }

    private fun setupCalendarView() {
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate.set(year, month, dayOfMonth)
            filterHabitsForSelectedDate()
        }
    }

    private fun setupHabitsRecyclerView() {
        habitAdapter = HabitAdapter(mutableListOf()) { habit ->
            habitViewModel.startEditingHabit(habit)
            navigateToEditScreen()
        }
        binding.recyclerViewHabits.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewHabits.adapter = habitAdapter
    }

    private fun navigateToEditScreen() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, AddHabitFragment()) // Use replace to ensure fresh state
            .addToBackStack(null)
            .commit()
    }

    private fun filterHabitsForSelectedDate() {
        val allHabits = habitViewModel.habits.value ?: emptyList()
        
        val filteredList = allHabits.filter { habit ->
            when (habit.repetitionType) {
                RepetitionType.DAILY -> true // Always show daily habits
                RepetitionType.WEEKLY -> {
                    val selectedDayOfWeek = when (selectedDate.get(Calendar.DAY_OF_WEEK)) {
                        Calendar.SUNDAY -> DayOfWeek.SUNDAY
                        Calendar.MONDAY -> DayOfWeek.MONDAY
                        Calendar.TUESDAY -> DayOfWeek.TUESDAY
                        Calendar.WEDNESDAY -> DayOfWeek.WEDNESDAY
                        Calendar.THURSDAY -> DayOfWeek.THURSDAY
                        Calendar.FRIDAY -> DayOfWeek.FRIDAY
                        Calendar.SATURDAY -> DayOfWeek.SATURDAY
                        else -> null
                    }
                    selectedDayOfWeek != null && habit.daysOfWeek?.contains(selectedDayOfWeek) == true
                }
                RepetitionType.ONCE -> {
                    if (habit.date == null) {
                        false
                    } else {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                        try {
                            val habitCalendar = Calendar.getInstance()
                            habitCalendar.time = dateFormat.parse(habit.date!!)!!
                            selectedDate.get(Calendar.YEAR) == habitCalendar.get(Calendar.YEAR) &&
                            selectedDate.get(Calendar.DAY_OF_YEAR) == habitCalendar.get(Calendar.DAY_OF_YEAR)
                        } catch (e: Exception) {
                            false
                        }
                    }
                }
            }
        }
        habitAdapter.updateHabits(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
