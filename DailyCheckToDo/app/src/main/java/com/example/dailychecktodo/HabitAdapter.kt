package com.example.dailychecktodo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dailychecktodo.databinding.ItemHabitBinding

class HabitAdapter(private var habits: MutableList<Habit>) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HabitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(habits[position])
    }

    override fun getItemCount() = habits.size

    fun updateHabits(newHabits: List<Habit>) {
        habits.clear()
        habits.addAll(newHabits)
        notifyDataSetChanged() // 通知 RecyclerView 更新畫面
    }

    class HabitViewHolder(private val binding: ItemHabitBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(habit: Habit) {
            binding.habitName.text = habit.name
            // 在這裡可以根據 habit.isCompleted 的狀態，設定完成按鈕的樣式
        }
    }
}