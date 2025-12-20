package com.example.dailychecktodo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

// Enum for the type of repetition
enum class RepetitionType {
    ONCE, // Happens only once on a specific date
    DAILY, // Repeats every day
    WEEKLY // Repeats on specific days of the week
}

// Enum for the days of the week
enum class DayOfWeek {
    SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY
}

@Parcelize
data class Habit(
    val id: String = UUID.randomUUID().toString(), // Unique ID for each habit
    val name: String,
    var notes: String? = null,
    var date: String? = null, // Used for ONCE type
    var time: String? = null,
    var priority: String? = null,
    var isPublic: Boolean = false,
    var isCompleted: Boolean = false,
    val repetitionType: RepetitionType = RepetitionType.ONCE,
    val daysOfWeek: List<DayOfWeek>? = null // Used for WEEKLY type
) : Parcelable
