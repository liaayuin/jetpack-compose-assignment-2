package com.example.todolistapp.data

data class Todo(
    val id: Int,
    val userId: Int,
    val title: String,
    val completed: Boolean,
    val description: String? = null
)