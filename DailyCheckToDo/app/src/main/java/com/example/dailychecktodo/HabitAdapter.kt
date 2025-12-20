package com.example.dailychecktodo

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dailychecktodo.databinding.ItemHabitBinding

class HabitAdapter(
    private var habits: MutableList<Habit>,
    private val onHabitClicked: (Habit) -> Unit // Callback for click events
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HabitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]
        holder.bind(habit)
        // Set the click listener on the item's root view
        holder.itemView.setOnClickListener {
            onHabitClicked(habit)
        }
    }

    override fun getItemCount() = habits.size

    fun updateHabits(newHabits: List<Habit>) {
        habits.clear()
        habits.addAll(newHabits)
        notifyDataSetChanged()
    }

    class HabitViewHolder(private val binding: ItemHabitBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(habit: Habit) {
            binding.habitName.text = habit.name
            binding.habitTime.text = habit.time

            // Set background color based on repetition type
            val color = when (habit.repetitionType) {
                RepetitionType.ONCE -> Color.parseColor("#FFFFE0") // Light Yellow
                RepetitionType.DAILY -> Color.parseColor("#E0FFE0") // Light Green
                RepetitionType.WEEKLY -> Color.parseColor("#E0E0FF") // Light Blue
            }
            binding.root.setBackgroundColor(color)
        }
    }
}