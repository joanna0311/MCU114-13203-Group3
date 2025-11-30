package com.example.dailychecktodo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Habit(
    val name: String,
    var notes: String? = null,
    var date: String? = null,
    var time: String? = null,
    var priority: String? = null,
    var isPublic: Boolean = false,
    var isCompleted: Boolean = false
) : Parcelable