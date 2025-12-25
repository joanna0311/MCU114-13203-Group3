package com.example.dailychecktodo

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dailychecktodo.databinding.ItemHabitBinding

class HabitAdapter(
    private var habits: MutableList<Habit>,
    private val onHabitAction: (Habit, Boolean) -> Unit // Callback for actions: Boolean is for completion change
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HabitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]
        holder.bind(habit, onHabitAction)
    }

    override fun getItemCount() = habits.size

    fun updateHabits(newHabits: List<Habit>) {
        habits.clear()
        habits.addAll(newHabits)
        notifyDataSetChanged()
    }

    fun getHabits(): List<Habit> {
        return habits
    }

    class HabitViewHolder(private val binding: ItemHabitBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(habit: Habit, onHabitAction: (Habit, Boolean) -> Unit) {
            binding.habitName.text = habit.name
            binding.habitTime.text = habit.time
            binding.habitCheckbox.isChecked = habit.isCompleted

            // Set priority indicator
            binding.habitPriorityIndicator.text = when (habit.priority) {
                "高" -> "!!!"
                "中" -> "!!"
                "低" -> "!"
                else -> ""
            }

            // Update UI based on completion status
            if (habit.isCompleted) {
                binding.habitName.paintFlags = binding.habitName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.root.setBackgroundColor(Color.LTGRAY)
            } else {
                binding.habitName.paintFlags = binding.habitName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                val color = when (habit.repetitionType) {
                    RepetitionType.ONCE -> Color.parseColor("#FFFFE0") // Light Yellow
                    RepetitionType.DAILY -> Color.parseColor("#E0FFE0") // Light Green
                    RepetitionType.WEEKLY -> Color.parseColor("#E0E0FF") // Light Blue
                }
                binding.root.setBackgroundColor(color)
            }

            // Listeners
            binding.root.setOnClickListener {
                onHabitAction(habit, false) // false means it's a click to edit
            }
            binding.habitCheckbox.setOnCheckedChangeListener { _, isChecked ->
                // To prevent the listener from firing when we bind the data
                if (habit.isCompleted != isChecked) {
                     onHabitAction(habit.copy(isCompleted = isChecked), true) // true means it's a completion change
                }
            }
        }
    }
}