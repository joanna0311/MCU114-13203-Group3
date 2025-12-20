package com.example.dailychecktodo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.dailychecktodo.databinding.FragmentAddHabitBinding
import com.google.android.material.chip.Chip

class AddHabitFragment : Fragment() {

    private var _binding: FragmentAddHabitBinding? = null
    private val binding get() = _binding!!

    private val habitViewModel: HabitViewModel by activityViewModels()

    private var isUpdatingFromObserver = false

    // Map to link Chip IDs to DayOfWeek enums
    private val chipIdToDayOfWeekMap by lazy {
        mapOf(
            R.id.chip_sun to DayOfWeek.SUNDAY,
            R.id.chip_mon to DayOfWeek.MONDAY,
            R.id.chip_tue to DayOfWeek.TUESDAY,
            R.id.chip_wed to DayOfWeek.WEDNESDAY,
            R.id.chip_thu to DayOfWeek.THURSDAY,
            R.id.chip_fri to DayOfWeek.FRIDAY,
            R.id.chip_sat to DayOfWeek.SATURDAY
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddHabitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return if (enter) {
            AnimationUtils.loadAnimation(context, R.anim.fade_in)
        } else {
            super.onCreateAnimation(transit, enter, nextAnim)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPriorityMenu()
        setupRepetitionMenu()
        setupDayOfWeekChips()
        setupViewModelObservers()
        setupViewListeners()
    }

    private fun setupViewModelObservers() {
        habitViewModel.editingHabit.observe(viewLifecycleOwner) { habit ->
            habit?.let { currentHabit ->
                isUpdatingFromObserver = true

                // Update basic fields
                if (binding.tilHabitTitle.editText?.text.toString() != currentHabit.name) {
                    binding.tilHabitTitle.editText?.setText(currentHabit.name)
                }
                if (binding.tilHabitNotes.editText?.text.toString() != currentHabit.notes) {
                    binding.tilHabitNotes.editText?.setText(currentHabit.notes)
                }
                if (binding.actvPriority.text.toString() != currentHabit.priority) {
                    binding.actvPriority.setText(currentHabit.priority, false)
                }

                // Update repetition UI
                val repetitionTypeName = when (currentHabit.repetitionType) {
                    RepetitionType.ONCE -> "當日"
                    RepetitionType.DAILY -> "每日"
                    RepetitionType.WEEKLY -> "每週"
                }
                if (binding.actvRepetitionType.text.toString() != repetitionTypeName) {
                    binding.actvRepetitionType.setText(repetitionTypeName, false)
                }
                binding.chipGroupDaysOfWeek.isVisible = currentHabit.repetitionType == RepetitionType.WEEKLY

                // Safely update chip states
                chipIdToDayOfWeekMap.forEach { (chipId, day) ->
                    val chip = binding.chipGroupDaysOfWeek.findViewById<Chip>(chipId)
                    chip?.isChecked = currentHabit.daysOfWeek?.contains(day) == true
                }

                isUpdatingFromObserver = false
            }
        }
    }

    private fun setupViewListeners() {
        binding.tilHabitTitle.editText?.doOnTextChanged { text, _, _, _ ->
            if (habitViewModel.editingHabit.value?.name != text.toString()) {
                updateHabit { it.copy(name = text.toString()) }
            }
        }

        binding.tilHabitNotes.editText?.doOnTextChanged { text, _, _, _ ->
            if (habitViewModel.editingHabit.value?.notes != text.toString()) {
                updateHabit { it.copy(notes = text.toString()) }
            }
        }

        binding.actvPriority.setOnItemClickListener { parent, _, position, _ ->
            val selectedPriority = parent.adapter.getItem(position) as String
            if (habitViewModel.editingHabit.value?.priority != selectedPriority) {
                updateHabit { it.copy(priority = selectedPriority) }
            }
        }

        binding.actvRepetitionType.setOnItemClickListener { _, _, position, _ ->
            val selectedType = RepetitionType.entries[position]
            if (habitViewModel.editingHabit.value?.repetitionType != selectedType) {
                updateHabit { it.copy(repetitionType = selectedType, daysOfWeek = if (selectedType == RepetitionType.WEEKLY) it.daysOfWeek else null) }
                binding.chipGroupDaysOfWeek.isVisible = selectedType == RepetitionType.WEEKLY
            }
        }

        binding.buttonDetails.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, HabitDetailsFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.buttonSave.setOnClickListener {
            habitViewModel.saveEditingHabit()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setupPriorityMenu() {
        val priorities = listOf("高", "中", "低")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, priorities)
        binding.actvPriority.setAdapter(adapter)
    }

    private fun setupRepetitionMenu() {
        val repetitionTypes = RepetitionType.entries.map { 
            when(it) {
                RepetitionType.ONCE -> "當日"
                RepetitionType.DAILY -> "每日"
                RepetitionType.WEEKLY -> "每週"
            }
        }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, repetitionTypes)
        binding.actvRepetitionType.setAdapter(adapter)
    }

    private fun setupDayOfWeekChips() {
        chipIdToDayOfWeekMap.keys.forEach { chipId ->
            val chip = binding.chipGroupDaysOfWeek.findViewById<Chip>(chipId)
            chip.setOnCheckedChangeListener { _, _ ->
                if (isUpdatingFromObserver) return@setOnCheckedChangeListener

                val selectedDays = chipIdToDayOfWeekMap.entries
                    .filter { binding.chipGroupDaysOfWeek.findViewById<Chip>(it.key).isChecked }
                    .map { it.value }
                
                updateHabit { it.copy(daysOfWeek = selectedDays) }
            }
        }
    }
    
    private fun updateHabit(modifier: (habit: Habit) -> Habit) {
        habitViewModel.editingHabit.value?.let {
            habitViewModel.updateEditingHabit(modifier(it))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
