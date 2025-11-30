package com.example.dailychecktodo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HabitViewModel : ViewModel() {

    // --- 主要習慣列表 ---
    private val _habits = MutableLiveData<MutableList<Habit>>().apply {
        value = mutableListOf()
    }
    val habits: LiveData<MutableList<Habit>> = _habits

    // --- 正在編輯或新增的習慣 ---
    private val _editingHabit = MutableLiveData<Habit?>()
    val editingHabit: LiveData<Habit?> = _editingHabit

    // --- 方法 ---

    // 開始新增一個全新的習慣
    fun startNewHabit() {
        _editingHabit.value = Habit(name = "") // 建立一個空的暫存習慣
    }

    // 更新正在編輯的習慣
    fun updateEditingHabit(habit: Habit) {
        _editingHabit.value = habit
    }

    // 將正在編輯的習慣儲存到主要列表中
    fun saveEditingHabit() {
        _editingHabit.value?.let { habitToSave ->
            val currentList = _habits.value ?: mutableListOf()
            currentList.add(habitToSave)
            _habits.value = currentList
        }
        _editingHabit.value = null // 清空暫存
    }
}