package com.example.dailychecktodo

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.util.Calendar

class ResetHabitsWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("ResetHabitsWorker", "Worker started.")

        // In a real application, you would get your repository or ViewModel here
        // to access your actual habit data.
        // For example: val habitRepository = (applicationContext as YourApplicationClass).repository
        // val habits = habitRepository.getAllHabits()

        // --- Start of the intelligent reset logic ---
        try {
            // 1. Get today's day of the week
            val todayCalendar = Calendar.getInstance()
            val today = when (todayCalendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SUNDAY -> DayOfWeek.SUNDAY
                Calendar.MONDAY -> DayOfWeek.MONDAY
                Calendar.TUESDAY -> DayOfWeek.TUESDAY
                Calendar.WEDNESDAY -> DayOfWeek.WEDNESDAY
                Calendar.THURSDAY -> DayOfWeek.THURSDAY
                Calendar.FRIDAY -> DayOfWeek.FRIDAY
                Calendar.SATURDAY -> DayOfWeek.SATURDAY
                else -> null
            }

            Log.d("ResetHabitsWorker", "Today is $today")

            // 2. This is a placeholder for your actual data.
            // You would fetch your list of habits from your database here.
            val habits = listOf<Habit>() // val habits = habitRepository.getAllHabits()

            val habitsToReset = mutableListOf<Habit>()

            // 3. Iterate and apply logic
            for (habit in habits) {
                var shouldReset = false
                when (habit.repetitionType) {
                    RepetitionType.DAILY -> {
                        shouldReset = true
                    }
                    RepetitionType.WEEKLY -> {
                        if (today != null && habit.daysOfWeek?.contains(today) == true) {
                            shouldReset = true
                        }
                    }
                    RepetitionType.ONCE -> {
                        // Do nothing for one-time habits
                    }
                }

                if (shouldReset && habit.isCompleted) {
                    habitsToReset.add(habit.copy(isCompleted = false))
                    Log.d("ResetHabitsWorker", "Resetting habit: ${habit.name}")
                }
            }

            // 4. Save the updated habits back to the database
            // if (habitsToReset.isNotEmpty()) {
            //     habitRepository.updateHabits(habitsToReset)
            // }

            Log.d("ResetHabitsWorker", "Work finished. ${habitsToReset.size} habits were reset.")
            return Result.success()

        } catch (e: Exception) {
            Log.e("ResetHabitsWorker", "Error during work", e)
            return Result.failure()
        }
        // --- End of the intelligent reset logic ---
    }
}
