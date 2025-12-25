package com.example.dailychecktodo

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HabitViewModel : ViewModel() {

    private val _habits = MutableLiveData<List<Habit>>(emptyList())
    val habits: LiveData<List<Habit>> = _habits

    private val _editingHabit = MutableLiveData<Habit?>()
    val editingHabit: LiveData<Habit?> = _editingHabit

    private lateinit var sharedPreferences: SharedPreferences
    private var currentUserEmail: String? = null
    private val gson = Gson()

    fun initForUser(context: Context, email: String) {
        currentUserEmail = email
        sharedPreferences = context.getSharedPreferences("habit_data", Context.MODE_PRIVATE)
        loadHabitsForCurrentUser()
    }

    private fun loadHabitsForCurrentUser() {
        currentUserEmail?.let {
            val json = sharedPreferences.getString("${it}_habits", null)
            if (json != null) {
                val type = object : TypeToken<List<Habit>>() {}.type
                val userHabits: List<Habit> = gson.fromJson(json, type)
                _habits.value = userHabits
            } else {
                _habits.value = emptyList() // No habits saved for this user yet
            }
        }
    }

    private fun saveHabitsToPreferences() {
        currentUserEmail?.let {
            val json = gson.toJson(_habits.value)
            sharedPreferences.edit().putString("${it}_habits", json).apply()
        }
    }

    fun startNewHabit(dateString: String) {
        _editingHabit.value = Habit(name = "", date = dateString)
    }

    fun startEditingHabit(habit: Habit) {
        _editingHabit.value = habit
    }

    fun updateEditingHabit(habit: Habit) {
        _editingHabit.value = habit
    }

    fun updateHabit(habitToUpdate: Habit) {
        val currentList = _habits.value.orEmpty().toMutableList()
        val index = currentList.indexOfFirst { it.id == habitToUpdate.id }
        if (index != -1) {
            currentList[index] = habitToUpdate
            _habits.value = currentList
            saveHabitsToPreferences() // Save after updating
        }
    }

    fun deleteHabit(habitToDelete: Habit) {
        val currentList = _habits.value.orEmpty().toMutableList()
        currentList.remove(habitToDelete)
        _habits.value = currentList
        saveHabitsToPreferences()
    }

    fun addHabit(habit: Habit) {
        val currentList = _habits.value.orEmpty().toMutableList()
        currentList.add(habit)
        _habits.value = currentList
        saveHabitsToPreferences()
    }

    fun deleteHabits(habitsToDelete: List<Habit>) {
        val currentList = _habits.value.orEmpty().toMutableList()
        val idsToDelete = habitsToDelete.map { it.id }.toSet()
        currentList.removeAll { idsToDelete.contains(it.id) }
        _habits.value = currentList
        saveHabitsToPreferences() // Save after deleting
    }

    fun saveEditingHabit() {
        val habitToSave = _editingHabit.value ?: return
        val currentList = _habits.value.orEmpty().toMutableList()

        val existingIndex = currentList.indexOfFirst { it.id == habitToSave.id }

        if (existingIndex != -1) {
            currentList[existingIndex] = habitToSave
        } else {
            currentList.add(habitToSave)
        }

        _habits.value = currentList
        _editingHabit.value = null
        saveHabitsToPreferences() // Save after adding/editing
    }
}