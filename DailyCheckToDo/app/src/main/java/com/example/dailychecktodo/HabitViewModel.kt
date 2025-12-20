package com.example.dailychecktodo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HabitViewModel : ViewModel() {

    private val _habits = MutableLiveData<List<Habit>>(emptyList())
    val habits: LiveData<List<Habit>> = _habits

    private val _editingHabit = MutableLiveData<Habit?>()
    val editingHabit: LiveData<Habit?> = _editingHabit

    fun startNewHabit(dateString: String) {
        _editingHabit.value = Habit(name = "", date = dateString)
    }

    fun startEditingHabit(habit: Habit) {
        _editingHabit.value = habit
    }

    fun updateEditingHabit(habit: Habit) {
        _editingHabit.value = habit
    }

    fun saveEditingHabit() {
        val habitToSave = _editingHabit.value ?: return
        val currentList = _habits.value.orEmpty().toMutableList()

        // Check if the habit already exists by its ID
        val existingIndex = currentList.indexOfFirst { it.id == habitToSave.id }

        if (existingIndex != -1) {
            // It's an existing habit, so update it at its position
            currentList[existingIndex] = habitToSave
        } else {
            // It's a new habit, so add it to the list
            currentList.add(habitToSave)
        }

        _habits.value = currentList
        _editingHabit.value = null
    }
}