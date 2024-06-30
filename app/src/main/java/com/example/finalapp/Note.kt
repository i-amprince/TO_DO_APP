package com.example.learningggg

import java.util.Date

data class Note(
    val id: Int,
    val title: String,
    val content: String,
    val dueDate: Date?,
    val priority: Priority,
    val completed: Boolean
)


enum class Priority {
    LOW, MEDIUM, HIGH
}
